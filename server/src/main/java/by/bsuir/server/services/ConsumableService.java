package by.bsuir.server.services;

import by.bsuir.server.db.dao.ConsumableDAO;
import by.bsuir.server.db.entities.Consumable;
import by.bsuir.server.utils.Nullifable;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.Response;
import by.bsuir.tcp.ResponseStatus;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

public class ConsumableService implements Nullifable {
    private Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();
    private ConsumableDAO consumableDAO = new ConsumableDAO();

    public Response getAllConsumables() {
        return Response.builder()
                .status(ResponseStatus.OK)
                .message("Расходники успешно получены!")
                .data(gson.toJson(consumableDAO.getAll()))
                .build();
    }

    public Response addConsumable(Request req) {
        Consumable consumable = gson.fromJson(req.getData(), Consumable.class);

        if (consumable == null) {
            return  Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: данные о расходнике не разобраны!")
                    .build();
        }

        if (!consumableDAO.getConsumablesByName(consumable.getName()).isEmpty()) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: расходник с таким названием уже существует!")
                    .build();
        }

        consumableDAO.save(consumable);

        return Response.builder()
                .status(ResponseStatus.OK)
                .message("Расходник успешно добавлен!")
                .data(gson.toJson(consumableDAO.getConsumablesByName(consumable.getName()).getFirst()))
                .build();
    }

    public Response getConsumableById(Request req) {
        long id;
        try {
            id = Long.parseLong(req.getData());
        } catch (NumberFormatException e) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Неправильный формат идентификатора!")
                    .build();
        }

        Consumable consumable = consumableDAO.getById(id);

        if (consumable == null) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("По идентификатору не располагается ни одного расходника!")
                    .build();
        }

        return Response.builder()
                .status(ResponseStatus.OK)
                .message("Расходник успешно найден!")
                .data(gson.toJson(consumable))
                .build();
    }

    public Response deleteConsumable(Request req) {
        Consumable consumable = gson.fromJson(req.getData(), Consumable.class);

        if (consumable == null) {
            return  Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: данные о расходнике не разобраны!")
                    .build();
        }

        consumable = consumableDAO.getById(consumable.getId());

        if (consumable == null) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Не найден расходник для удаления!")
                    .build();
        }

        consumableDAO.delete(consumable);

        return Response.builder()
                .status(ResponseStatus.OK)
                .message("Расходник успешно удалён!")
                .build();
    }

    public Response updateConsumable(Request req) {
        Consumable consumable = gson.fromJson(req.getData(), Consumable.class);

        if (consumable == null) {
            return  Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: данные о расходнике не разобраны!")
                    .build();
        }

        consumableDAO.update(consumable);

        return Response.builder()
                .status(ResponseStatus.OK)
                .message("Расходник успешно обновлён!")
                .build();
    }

    @Override
    public void nullify() {
        gson = null;
        consumableDAO = null;

        System.gc();
    }
}
