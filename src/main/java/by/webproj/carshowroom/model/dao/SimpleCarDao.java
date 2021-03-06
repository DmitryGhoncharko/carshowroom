package by.webproj.carshowroom.model.dao;

import by.webproj.carshowroom.entity.Car;
import by.webproj.carshowroom.exception.DaoException;
import by.webproj.carshowroom.model.connection.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class SimpleCarDao implements CarDao {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleCarDao.class);
    private static final String SQL_ADD_CAR = "insert into car(car_name, car_description) values (?,?)";
    private static final String SQL_DELETE_CAR = "delete from car where car_id = ?";
    private static final String SQL_UPDATE_CAR = "update car set car_name = ?, car_description = ? where car_id = ?";
    private static final String SQL_GET_ALL_CARS = "select car_id, car_name, car_description from  car";
    private static final String SQL_FIND_CAR_BY_ID = "select car_id, car_name, car_description from car where car_id = ?";
    private final ConnectionPool connectionPool;

    public SimpleCarDao(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public Car addCar(Car car) throws DaoException {
        try (final Connection connection = connectionPool.getConnection(); final PreparedStatement preparedStatement = connection.prepareStatement(SQL_ADD_CAR, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, car.getCarName());
            preparedStatement.setString(2, car.getCarDescription());
            final int countRowsCreated = preparedStatement.executeUpdate();
            final ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (countRowsCreated > 0 && generatedKeys.next()) {
                final Car createdCar = new Car.Builder().
                        withCarId(generatedKeys.getLong(1)).
                        withCarName(car.getCarName()).
                        withCarDescription(car.getCarDescription()).
                        build();
                return createdCar;
            }
        } catch (SQLException sqlException) {
            LOG.error("Cannot add new car, carName: " + car.getCarName() + " carDescription: " + car.getCarDescription(), sqlException);
            throw new DaoException("Cannot add new car, carName: " + car.getCarName() + " carDescription: " + car.getCarDescription(), sqlException);
        }
        LOG.error("Cannot add new car, carName: " + car.getCarName() + " carDescription: " + car.getCarDescription());
        throw new DaoException("Cannot add new car, carName: " + car.getCarName() + " carDescription: " + car.getCarDescription());
    }

    @Override
    public boolean deleteCar(long carId) throws DaoException {
        try (final Connection connection = connectionPool.getConnection(); final PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE_CAR)) {
            preparedStatement.setLong(1, carId);
            final int countRowsDeleted = preparedStatement.executeUpdate();
            return countRowsDeleted > 0;
        } catch (SQLException sqlException) {
            LOG.error("Cannot delete car by id carId: " + carId, sqlException);
            throw new DaoException("Cannot delete car by id carId: " + carId, sqlException);
        }
    }

    @Override
    public Car updateCar(Car car) throws DaoException {
        try (final Connection connection = connectionPool.getConnection(); final PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE_CAR)) {
            preparedStatement.setString(1, car.getCarName());
            preparedStatement.setString(2, car.getCarDescription());
            preparedStatement.setLong(3, car.getCarId());
            final int countUpdatedRows = preparedStatement.executeUpdate();
            if (countUpdatedRows > 0) {
                return car;
            }
        } catch (SQLException sqlException) {
            LOG.error("Cannot update car by id, carId: " + car.getCarId() + " carName: " + car.getCarName() + " carDescription: " + car.getCarDescription(), sqlException);
            throw new DaoException("Cannot update car by id, carId: " + car.getCarId() + " carName: " + car.getCarName() + " carDescription: " + car.getCarDescription(), sqlException);
        }
        LOG.error("Cannot update car by id, carId: " + car.getCarId() + " carName: " + car.getCarName() + " carDescription: " + car.getCarDescription());
        throw new DaoException("Cannot update car by id, carId: " + car.getCarId() + " carName: " + car.getCarName() + " carDescription: " + car.getCarDescription());
    }

    @Override
    public List<Car> getCars() throws DaoException {
        final List<Car> carList = new ArrayList<>();
        try (final Connection connection = connectionPool.getConnection(); final Statement statement = connection.createStatement()) {
            final ResultSet resultSet = statement.executeQuery(SQL_GET_ALL_CARS);
            while (resultSet.next()) {
                final Car car = new Car.Builder().
                        withCarId(resultSet.getLong(1)).
                        withCarName(resultSet.getString(2)).
                        withCarDescription(resultSet.getString(3)).
                        build();
                carList.add(car);
            }
        } catch (SQLException sqlException) {
            LOG.error("Cannot get all cars", sqlException);
            throw new DaoException("Cannot get all cars", sqlException);
        }
        return carList;
    }

    @Override
    public Optional<Car> findCarById(long carId) throws DaoException {
        try (final Connection connection = connectionPool.getConnection(); final PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_CAR_BY_ID)) {
            preparedStatement.setLong(1, carId);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                final Car car = new Car.Builder().
                        withCarId(resultSet.getLong(1)).
                        withCarName(resultSet.getString(2)).
                        withCarDescription(resultSet.getString(3)).
                        build();
                return Optional.of(car);
            }
        } catch (SQLException sqlException) {
            LOG.error("Cannot find car by id, carId:" + carId, sqlException);
            throw new DaoException("Cannot find car by id, carId:" + carId, sqlException);
        }
        LOG.info("Cannot find car by id, carId:" + carId);
        return Optional.empty();
    }
}
