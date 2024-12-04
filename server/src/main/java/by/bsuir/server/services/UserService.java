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

    public void login(Request req, Response res) {
        final User userFromRequest = gson.fromJson(req.getData(), User.class);

        User user = userDao.getAll().stream()
                .filter(x -> x.getLogin().equals(userFromRequest.getLogin()) && x.getPassword().equals(userFromRequest.getPassword()))
                .findFirst()
                .orElse(null);

        if (user != null) {
            res.setStatus(ResponseStatus.OK);
            res.setMessage("Успешный вход");
            res.setData(gson.toJson(user));
        } else {
            res.setStatus(ResponseStatus.ERROR);
            res.setMessage("Пользователь с указанными данными не найден");
        }
    }

    public void register(Request req, Response res) {

    }

    public void update(Request req, Response res) {

    }

    @Override
    public void nullify() {
        userDao = null;
        gson = null;

        System.gc();
    }
}
