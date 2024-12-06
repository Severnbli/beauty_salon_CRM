package by.bsuir.server.services;

import by.bsuir.server.db.dao.OrderDAO;
import by.bsuir.server.db.entities.Order;
import by.bsuir.server.db.entities.StatusOfRecord;
import by.bsuir.server.utils.Nullifable;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.Response;
import by.bsuir.tcp.ResponseStatus;
import com.google.gson.Gson;

public class OrderService implements Nullifable {
    private OrderDAO orderDAO = new OrderDAO();
    private Gson gson = new Gson();

    public Response rejectOrder(Request req) {
        final Order orderFromRequest = gson.fromJson(req.getData(), Order.class);

        if (orderFromRequest == null) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: данные о пользователе не разобраны!")
                    .build();
        }

        final Order order = orderDAO.getById(orderFromRequest.getId());

        if (order == null) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Указанная запись не найдена в БД!")
                    .build();
        }

        if (order.getStatusOfRecord() != StatusOfRecord.REGISTERED) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Указанную запись не требуется отменять!")
                    .build();
        }

        order.setStatusOfRecord(StatusOfRecord.REJECTED);

        orderDAO.update(order);

        return Response.builder()
                .status(ResponseStatus.OK)
                .message("Отмена записи осуществлена успешно!")
                .build();
    }

    @Override
    public void nullify() {
        orderDAO = null;
        gson = null;

        System.gc();
    }
}
