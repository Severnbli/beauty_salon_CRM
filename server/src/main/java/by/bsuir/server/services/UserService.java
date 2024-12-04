package by.bsuir.server.services;

import by.bsuir.server.db.dao.PersonDataDAO;
import by.bsuir.server.db.dao.RoleDAO;
import by.bsuir.server.db.dao.UserDAO;
import by.bsuir.server.db.entities.PersonData;
import by.bsuir.server.db.entities.Role;
import com.google.gson.Gson;
import by.bsuir.server.db.entities.User;
import by.bsuir.tcp.ResponseStatus;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.Response;
import by.bsuir.server.utils.Nullifable;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class UserService implements Nullifable {
    private PersonDataDAO personDataDao = new PersonDataDAO();
    private UserDAO userDao = new UserDAO();
    private Gson gson = new Gson();

    public Response login(Request req) {
        final User userFromRequest = gson.fromJson(req.getData(), User.class);

        if (userFromRequest == null) {
            return  Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: данные о пользователе не разобраны!")
                    .build();
        }

        User user = userDao.getUserWithSuchLogin(userFromRequest.getLogin());

        if (user != null && BCrypt.checkpw(userFromRequest.getPassword(), user.getPassword())) {
            return Response.builder()
                    .status(ResponseStatus.OK)
                    .message("Успешный вход!")
                    .data(gson.toJson(user))
                    .build();
        } else {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Пользователь с указанными данными не найден!")
                    .build();
        }
    }

    public Response register(Request req) {
        final User userFromRequest = gson.fromJson(req.getData(), User.class);

        if (userFromRequest == null || userFromRequest.getRole() == null || userFromRequest.getPersonData() == null) {
            return  Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: данные о пользователе не разобраны!")
                    .build();
        }

        if (userDao.getUserWithSuchLogin(userFromRequest.getLogin()) != null) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Пользователь с введённым логином существует!")
                    .build();
        }

        userFromRequest.setPassword(BCrypt.hashpw(userFromRequest.getPassword(), BCrypt.gensalt()));

        personDataDao.save(userFromRequest.getPersonData());
        userDao.save(userFromRequest);

        if (userDao.getUserWithSuchLogin(userFromRequest.getLogin()) != null) {
            return Response.builder()
                    .status(ResponseStatus.OK)
                    .message("Регистрация успешна!")
                    .build();
        } else {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Регистрация не удалась!")
                    .build();
        }
    }

    public Response updateProfile(Request req) {
        final User userFromRequest = gson.fromJson(req.getData(), User.class);

        if (userFromRequest == null || userFromRequest.getPersonData() == null) {
            return  Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: данные о пользователе не разобраны!")
                    .build();
        }

        userDao.update(userFromRequest);
        personDataDao.update(userFromRequest.getPersonData());

        return Response.builder()
                .status(ResponseStatus.OK)
                .message("Обновление данных успешно!")
                .data(gson.toJson(userFromRequest))
                .build();
    }

    @Override
    public void nullify() {
        personDataDao = null;
        userDao = null;
        gson = null;

        System.gc();
    }
}
