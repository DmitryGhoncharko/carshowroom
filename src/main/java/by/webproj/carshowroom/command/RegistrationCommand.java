package by.webproj.carshowroom.command;

import by.webproj.carshowroom.controller.RequestFactory;
import by.webproj.carshowroom.entity.User;
import by.webproj.carshowroom.exception.ServiceError;
import by.webproj.carshowroom.mailsender.secretkeycache.SecretKeyCache;
import by.webproj.carshowroom.model.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class RegistrationCommand implements Command {
    private static final Logger LOG = LoggerFactory.getLogger(RegistrationCommand.class);
    private final UserService userService;
    private final RequestFactory requestFactory;

    public RegistrationCommand(UserService userService, RequestFactory requestFactory) {
        this.userService = userService;
        this.requestFactory = requestFactory;
    }

    @Override
    public CommandResponse execute(CommandRequest request) throws ServiceError {
        final String login = request.getParameter("login");
        final String password = request.getParameter("password");
        final String secretCode = request.getParameter("secretcode");
        userService.addUserAsAdmin(login, password, secretCode);
        return requestFactory.createRedirectResponse(PagePath.INDEX_PATH.getPath());
    }
}
