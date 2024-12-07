package by.bsuir.server.services;

import by.bsuir.server.db.dao.MasterDAO;
import by.bsuir.server.db.entities.Master;
import by.bsuir.server.utils.Nullifable;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.Response;
import by.bsuir.tcp.ResponseStatus;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MasterServices implements Nullifable {
    private Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();
    private MasterDAO masterDAO = new MasterDAO();

    public Response getMasterById(Request req) {
        long id;

        try {
            id = Long.parseLong(req.getData());
        } catch (NumberFormatException e) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: не удалось преобразовать к целому!")
                    .build();
        }

        Master master = masterDAO.getById(id);

        if (master == null) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: мастер по запросу не найден!")
                    .build();
        } else {
            return Response.builder()
                    .status(ResponseStatus.OK)
                    .message("Мастер по запросу успешно найден!")
                    .data(gson.toJson(master))
                    .build();
        }
    }

    @Override
    public void nullify() {
        gson = null;
        masterDAO = null;

        System.gc();
    }
}
