package by.bsuir.server.db.utils;

import com.google.gson.Gson;
import by.bsuir.server.db.entities.User;
import by.bsuir.server.db.services.UserService;
import by.bsuir.enums.ResponseStatus;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.Response;
import by.bsuir.server.utils.Nullifable;

public class UserUtilities implements Nullifable {
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

    @Override
    public void nullify() {
        userService = null;
        gson = null;

        System.gc();
    }
}
