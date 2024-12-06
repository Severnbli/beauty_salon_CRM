package by.bsuir.server.services;

import by.bsuir.server.db.dao.ServiceDAO;
import by.bsuir.server.db.entities.Service;
import by.bsuir.server.utils.Nullifable;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.Response;
import by.bsuir.tcp.ResponseStatus;
import com.google.gson.Gson;

import java.util.List;

public class ServiceService implements Nullifable {
    private Gson gson = new Gson();
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

    @Override
    public void nullify() {
        gson = null;
        serviceDAO = null;

        System.gc();
    }
}
