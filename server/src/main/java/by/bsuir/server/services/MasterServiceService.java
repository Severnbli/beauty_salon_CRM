package by.bsuir.server.services;

import by.bsuir.server.db.dao.MasterScheduleDAO;
import by.bsuir.server.db.dao.MasterServiceDAO;
import by.bsuir.server.db.dao.OrderDAO;
import by.bsuir.server.db.entities.*;
import by.bsuir.server.utils.Nullifable;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.Response;
import by.bsuir.tcp.ResponseStatus;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MasterServiceService implements Nullifable {
    MasterScheduleService masterScheduleService = new MasterScheduleService();
    private MasterServiceDAO masterServiceDAO = new MasterServiceDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private Gson gson = new Gson();

    public Response getMastersByServiceAndDate(Request req) {
        final Order orderFromRequest = gson.fromJson(req.getData(), Order.class);

        if (orderFromRequest == null) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: данные о бронировании не разобраны!")
                    .build();
        }

        List<Master> masters = masterServiceDAO.getMastersByServiceId(orderFromRequest.getService().getId());

        if (masters.isEmpty()) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: нет ни одного мастера, который оказывает эту услугу!")
                    .build();
        }

        final List<MasterSchedule> mastersSchedules = masterScheduleService.getMastersSchedulesByMastersIdsAndDayOfWeek(
                masters,
                DayOfWeek.getDayOfWeekByDate(LocalDate.from(orderFromRequest.getDate()))
        );

        if (mastersSchedules.isEmpty()) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Не найдено ни одного работающего мастера на запрашиваемый день!")
                    .build();
        }

        final HashMap<Master, List<LocalTime>> resultMastersTimes = new HashMap<>();

        for (MasterSchedule masterSchedule : mastersSchedules) {
            final List<Order> ordersWhichMasterInvolved = orderDAO.getOrdersByMasterAndDate(
                    masterSchedule.getMaster().getId(),
                    LocalDate.from(orderFromRequest.getDate())
            );

            List<LocalTime> availableTimes = findAvailableTimes(
                    masterSchedule,
                    ordersWhichMasterInvolved,
                    orderFromRequest.getService());

            if (!availableTimes.isEmpty()) {
                resultMastersTimes.put(masterSchedule.getMaster(), availableTimes);
            }
        }

        if (resultMastersTimes.isEmpty()) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Не найдено ни одного свободного мастера!")
                    .build();
        }

        return Response.builder()
                .status(ResponseStatus.OK)
                .message("Мастера успешно найдены!")
                .data(gson.toJson(resultMastersTimes))
                .build();
    }

    public static List<LocalTime> findAvailableTimes(MasterSchedule masterSchedule, List<Order> orders, Service service) {
        List<LocalTime> availableTimes = new ArrayList<>();

        LocalTime serviceDuration = service.getTimeCost();

        for (LocalTime currentTime = masterSchedule.getStartTime();
             !currentTime.isAfter(masterSchedule.getEndTime().minusSeconds(serviceDuration.toSecondOfDay()));
             currentTime = currentTime.plusMinutes(30)) {

            final LocalTime finalCurrentTime = currentTime;
            boolean isTimeOccupied = orders.stream().anyMatch(order -> {
                LocalTime orderStart = order.getDate().toLocalTime();
                LocalTime orderEnd = orderStart.plusSeconds(order.getService().getTimeCost().toSecondOfDay());
                return !(finalCurrentTime.plusSeconds(serviceDuration.toSecondOfDay()).isBefore(orderStart) || finalCurrentTime.isAfter(orderEnd));
            });

            if (!isTimeOccupied) {
                availableTimes.add(currentTime);
            }
        }

        return availableTimes;
    }

    @Override
    public void nullify() {
        masterScheduleService.nullify();

        masterServiceDAO = null;
        masterScheduleService = null;
        orderDAO = null;
        gson = null;

        System.gc();
    }
}
