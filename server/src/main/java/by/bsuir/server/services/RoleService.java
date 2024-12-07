package by.bsuir.server.services;

import by.bsuir.server.db.dao.RoleDAO;
import by.bsuir.server.db.entities.Role;
import by.bsuir.server.db.entities.User;
import by.bsuir.server.utils.Nullifable;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.Response;
import by.bsuir.tcp.ResponseStatus;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RoleService implements Nullifable {
    private RoleDAO roleDAO = new RoleDAO();
    private Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();

    public Response roleByAccessLevel(Request req) {
        int accessLevel;

        try {
            accessLevel = Integer.parseInt(req.getData());
        } catch (NumberFormatException e) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Приведение типов не сработало!")
                    .build();
        }

        Role role = roleDAO.getByAccessLevel(accessLevel);

        if (role == null) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Не найдена запрашиваемая роль!")
                    .build();
        }

        return Response.builder()
                .status(ResponseStatus.OK)
                .data(gson.toJson(role))
                .build();
    }

    @Override
    public void nullify() {
        roleDAO = null;
        gson = null;

        System.gc();
    }
}
