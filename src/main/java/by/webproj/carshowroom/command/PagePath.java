package by.webproj.carshowroom.command;

public enum PagePath {
    MAIN_PAGE("/WEB-INF/jsp/main.jsp"), LOGIN_PAGE("/WEB-INF/jsp/login.jsp"), INDEX_PATH("/"), CARS_PAGE("/WEB-INF/jsp/cars.jsp"),
    ADD_CAR_PAGE("/WEB-INF/jsp/addcar.jsp"), SECRETCODE_PAGE("/WEB-INF/jsp/secretcode.jsp"), REGISTRATION_PAGE("/WEB-INF/jsp/registration.jsp"),
    UPDATE_CAR_PAGE("/WEB-INF/jsp/updateCar.jsp"), USERS_PAGE("/WEB-INF/jsp/users.jsp"), USER_FAVORITES_PAGE("/WEB-INF/jsp/userFavorites.jsp");
    private final String path;

    PagePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
