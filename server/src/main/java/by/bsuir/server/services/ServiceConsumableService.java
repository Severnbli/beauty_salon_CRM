package by.bsuir.server.services;

import by.bsuir.server.db.dao.ServiceConsumableDAO;
import by.bsuir.server.db.entities.Consumable;
import by.bsuir.server.db.entities.Service;
import by.bsuir.server.db.entities.ServiceConsumable;
import by.bsuir.server.utils.Nullifable;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.Response;
import by.bsuir.tcp.ResponseStatus;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

public class ServiceConsumableService implements Nullifable {
    private Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();
    private ServiceConsumableDAO serviceConsumableDao = new ServiceConsumableDAO();

    public Response getConsumablesByService(Request req) {
        Service service = gson.fromJson(req.getData(), Service.class);

        if (service == null) {
            return  Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: данные об услуге не разобраны!")
                    .build();
        }

        List<Consumable> consumables = serviceConsumableDao.getConsumablesByServiceId(service.getId());

        return Response.builder()
                .status(ResponseStatus.OK)
                .data(gson.toJson(consumables))
                .build();
    }

    public Response delServiceConsumable(Request req) {
        ServiceConsumable serviceConsumable = gson.fromJson(req.getData(), ServiceConsumable.class);

        if (serviceConsumable == null) {
            return  Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: данные об услуге / расходнике не разобраны!")
                    .build();
        }

        serviceConsumable = serviceConsumableDao.getByServiceAndConsumable(serviceConsumable);

        if (serviceConsumable == null) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Не найдено пары услуга - расходник!")
                    .build();
        }

        serviceConsumableDao.delete(serviceConsumable);

        return Response.builder()
                .status(ResponseStatus.OK)
                .message("Удаление расходника из услуги успешно!")
                .build();
    }

    public Response addServiceConsumable(Request req) {
        ServiceConsumable serviceConsumable = gson.fromJson(req.getData(), ServiceConsumable.class);

        if (serviceConsumable == null) {
            return  Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: данные об услуге / расходнике не разобраны!")
                    .build();
        }

        if (serviceConsumableDao.getByServiceAndConsumable(serviceConsumable) != null) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Пара услуга - расходник уже существует!")
                    .build();
        }

        serviceConsumableDao.save(serviceConsumable);

        return Response.builder()
                .status(ResponseStatus.OK)
                .message("Расходник успешно добавлен к услуге!")
                .build();
    }

    @Override
    public void nullify() {
        serviceConsumableDao = null;
        gson = null;

        System.gc();
    }
}
