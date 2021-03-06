package by.webproj.carshowroom.model.dao;

import by.webproj.carshowroom.entity.Car;
import by.webproj.carshowroom.exception.DaoException;

import java.util.List;
import java.util.Optional;

public interface CarDao {
   Car addCar(Car car) throws DaoException;
   boolean deleteCar(long carId) throws DaoException;
   Car updateCar(Car car) throws DaoException;
   List<Car> getCars() throws DaoException;

   Optional<Car> findCarById(long carId) throws DaoException;
}
