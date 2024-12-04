package by.bsuir.server.services;

import by.bsuir.server.db.dao.UserDAO;
import com.google.gson.Gson;
import by.bsuir.server.db.entities.User;
import by.bsuir.tcp.ResponseStatus;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.Response;
import by.bsuir.server.utils.Nullifable;

public class UserService implements Nullifable {
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

        User user = userDao.getUserWithSuchLoginAndPassword(userFromRequest.getLogin(), userFromRequest.getPassword());

        if (user != null) {
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
        return null;
    }

    public Response update(Request req) {
        return null;
    }

    @Override
    public void nullify() {
        userDao = null;
        gson = null;

        System.gc();
    }
    }
