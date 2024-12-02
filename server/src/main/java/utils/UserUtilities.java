package utils;

import com.google.gson.Gson;
import db.entities.User;
import db.services.UserService;
import enums.ResponseStatus;
import tcp.Request;
import tcp.Response;

public class UserUtilities {
    private UserService userService = new UserService();
    private Gson gson = new Gson();

    public void login(Request req, Response res) {
        final User userFromRequest = gson.fromJson(req.getData(), User.class);

        User user = userService.getAll().stream()
                .filter(x -> x.getLogin().equals(userFromRequest.getLogin()) && x.getPassword().equals(userFromRequest.getPassword()))
                .findFirst()
                .orElse(null);

        if (user != null) {
            res = new Response(ResponseStatus.OK, "Успешный вход", gson.toJson(user));
        } else {
            res = new Response(ResponseStatus.ERROR, "Пользователь с указанными данными не найден", "");
        }
    }

    public void register(Request req, Response res) {

    }

    public void update(Request req, Response res) {

    }
}
