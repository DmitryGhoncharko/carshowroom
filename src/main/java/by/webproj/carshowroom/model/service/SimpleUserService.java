package by.webproj.carshowroom.model.service;

import by.webproj.carshowroom.entity.Role;
import by.webproj.carshowroom.entity.User;
import by.webproj.carshowroom.exception.DaoException;
import by.webproj.carshowroom.exception.ServiceError;
import by.webproj.carshowroom.mailsender.secretkeycache.SecretKeyCache;
import by.webproj.carshowroom.model.dao.UserDao;
import by.webproj.carshowroom.securiy.PasswordHasher;
import by.webproj.carshowroom.validator.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class SimpleUserService implements UserService {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleUserService.class);
    private final UserValidator userValidator;
    private final UserDao userDao;
    private final PasswordHasher passwordHasher;
    private final SecretKeyCache secretKeyCache;

    public SimpleUserService(UserValidator userValidator, UserDao userDao, PasswordHasher passwordHasher, SecretKeyCache secretKeyCache) {
        this.userValidator = userValidator;
        this.userDao = userDao;
        this.passwordHasher = passwordHasher;
        this.secretKeyCache = secretKeyCache;
    }

    @Override
    public User addUserAsAdmin(String login, String password, String secretKey) {
        if (!userValidator.validateUserDataByLoginAndPasswordWithSecretKey(login, password, secretKey)) {
            throw new ServiceError("Invalid user data, userPassword: " + login + " userLogin: " + password + " secretKey: " + secretKey);
        }
        try {
            if (secretKeyCache.containKeyOnCache(secretKey)) {
                final String hashedPassword = passwordHasher.hashPassword(password);
                final User user = new User.Builder().
                        withUserLogin(login).
                        withUserPassword(hashedPassword).
                        withUserRole(Role.ADMIN).
                        build();
                secretKeyCache.deleteKeyFromCache(secretKey);
                return userDao.addUser(user);
            }
        } catch (DaoException daoException) {
            LOG.error("Cannot add new user, userLogin: " + login + " userPassword: " + password + " secretKey: " + secretKey, daoException);
            throw new ServiceError("Cannot add new user, userLogin: " + login + " userPassword: " + password + " secretKey: " + secretKey, daoException);
        }
        LOG.error("Cannot add new user, userLogin: " + login + " userPassword: " + password + " secretKey: " + secretKey);
        throw new ServiceError("Cannot add new user, userLogin: " + login + " userPassword: " + password + " secretKey: " + secretKey);
    }

    @Override
    public Optional<User> authenticateIfAdmin(String login, String password) {
        if (!userValidator.validateUserDataByLoginAndPassword(login, password)) {
            return Optional.empty();
        }
        try {

            final Optional<User> userFromDB = userDao.findUserByLogin(login);
            if (userFromDB.isPresent()) {
                final User userInstance = userFromDB.get();
                final String hashedPasswordFromDB = userInstance.getUserPassword();
                if (userInstance.getUserRole().equals(Role.ADMIN) && passwordHasher.checkIsEqualsPasswordAndPasswordHash(password, hashedPasswordFromDB)) {
                    return userFromDB;
                }
            }
        } catch (DaoException daoException) {
            LOG.error("Cannot authorize user, userLogin: " + login + " userPassword :" + password, daoException);
            throw new ServiceError("Cannot authorize user, userLogin: " + login + " userPassword :" + password);
        }
        return Optional.empty();
    }
}
