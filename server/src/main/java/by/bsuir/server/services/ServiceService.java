package by.bsuir.server.services;

import by.bsuir.server.db.dao.ServiceDAO;
import by.bsuir.server.db.entities.Service;
import by.bsuir.server.utils.Nullifable;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.Response;
import by.bsuir.tcp.ResponseStatus;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

public class ServiceService implements Nullifable {
    private Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();
    private ServiceDAO serviceDAO = new ServiceDAO();

    public Response getAllServices() {
        List<Service> services = serviceDAO.getAll();

        if (services.isEmpty()) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Нет ни одной услуги!")
                    .build();
        }

        return Response.builder()
                .status(ResponseStatus.OK)
                .data(gson.toJson(services))
                .build();
    }

    public Response addService(Request req) {
        Service service = gson.fromJson(req.getData(), Service.class);

        if (service == null) {
            return  Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: данные об услуге не разобраны!")
                    .build();
        }

        if (serviceDAO.getByName(service.getName()) != null) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Нельзя добавить услугу с существуюшим названием!")
                    .build();
        }

        serviceDAO.save(service);

        return Response.builder()
                .status(ResponseStatus.OK)
                .message("Услуга успешно добавлена!")
                .build();
    }

    public Response delService(Request req) {
        Service service = gson.fromJson(req.getData(), Service.class);

        if (service == null) {
            return  Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: данные об услуге не разобраны!")
                    .build();
        }

        if (serviceDAO.getById(service.getId()) == null) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Услуга не определена.")
                    .build();
        }

        serviceDAO.delete(service);

        return Response.builder()
                .status(ResponseStatus.OK)
                .message("Услуга удалена успешно!")
                .build();
    }

    public Response updateService(Request req) {
        Service service = gson.fromJson(req.getData(), Service.class);

        if (service == null) {
            return  Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: данные об услуге не разобраны!")
                    .build();
        }

        if (serviceDAO.getById(service.getId()) == null) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Услуга не определена.")
                    .build();
        }

        serviceDAO.update(service);

        return Response.builder()
                .status(ResponseStatus.OK)
                .message("Услуга обновлена успешно!")
                .build();
    }

    @Override
    public void nullify() {
        gson = null;
        serviceDAO = null;

        System.gc();
    }
}
