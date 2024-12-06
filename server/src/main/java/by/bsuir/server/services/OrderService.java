package by.bsuir.server.services;

import by.bsuir.server.db.dao.OrderDAO;
import by.bsuir.server.db.dao.UserDAO;
import by.bsuir.server.db.entities.Order;
import by.bsuir.server.db.entities.StatusOfRecord;
import by.bsuir.server.db.entities.User;
import by.bsuir.server.utils.Nullifable;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.Response;
import by.bsuir.tcp.ResponseStatus;
import com.google.gson.Gson;

import java.util.List;

public class OrderService implements Nullifable {
    private OrderDAO orderDAO = new OrderDAO();
    private UserDAO userDAO = new UserDAO();
    private Gson gson = new Gson();

    public Response rejectOrder(Request req) {
        final Order orderFromRequest = gson.fromJson(req.getData(), Order.class);

        if (orderFromRequest == null) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: данные о записи не разобраны!")
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

    public Response getOrdersByClientId(Request req) {
        final User userFromRequest = gson.fromJson(req.getData(), User.class);

        if (userFromRequest == null) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: данные о пользователе не разобраны!")
                    .build();
        }

        if (userDAO.getById(userFromRequest.getId()) == null) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: пользователь не существует!")
                    .build();
        }

        List<Order> orders = orderDAO.getOrdersByClientId(userFromRequest.getId());
        if (orders.isEmpty()) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Не найдено ни одной записи!")
                    .build();
        }

        return Response.builder()
                .status(ResponseStatus.OK)
                .data(gson.toJson(orders))
                .build();
    }

    @Override
    public void nullify() {
        orderDAO = null;
        userDAO = null;
        gson = null;

        System.gc();
    }
}
