package by.bsuir.server.services;

import by.bsuir.server.db.dao.MasterScheduleDAO;
import by.bsuir.server.db.dao.MasterServiceDAO;
import by.bsuir.server.db.dao.OrderDAO;
import by.bsuir.server.db.entities.*;
import by.bsuir.server.utils.Nullifable;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.Response;
import by.bsuir.tcp.ResponseStatus;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MasterServiceService implements Nullifable {
    MasterScheduleService masterScheduleService = new MasterScheduleService();
    private MasterServiceDAO masterServiceDAO = new MasterServiceDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();

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

        final HashMap<Long, List<LocalTime>> resultMastersTimes = new HashMap<>();

        for (MasterSchedule masterSchedule : mastersSchedules) {
            final List<Order> ordersWhichMasterInvolved = orderDAO.getOrdersByMasterAndDate(
                    masterSchedule.getMaster().getId(),
                    LocalDate.from(orderFromRequest.getDate())
            );

            List<LocalTime> availableTimes = findAvailableTimes(
                    masterSchedule,
                    ordersWhichMasterInvolved,
                    orderFromRequest.getService(),
                    LocalDate.from(orderFromRequest.getDate())
            );

            if (!availableTimes.isEmpty()) {
                resultMastersTimes.put(masterSchedule.getMaster().getId(), availableTimes);
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

    public static List<LocalTime> findAvailableTimes(
            MasterSchedule masterSchedule,
            List<Order> orders,
            Service service,
            LocalDate fromDate
    ) {
        List<LocalTime> availableTimes = new ArrayList<>();

        LocalTime serviceDuration = service.getTimeCost();

        LocalTime startTime = masterSchedule.getStartTime();

        LocalTime nowTime = LocalTime.now();

        if (LocalDate.now().isEqual(fromDate) && nowTime.isAfter(startTime)) {
            if (LocalTime.now().getMinute() > 30) {
                startTime = nowTime.withHour(nowTime.getHour() + 1).withMinute(0).withSecond(0).withNano(0);
            } else {
                startTime = nowTime.withMinute(30).withSecond(0).withNano(0);
            }
        }

        for (LocalTime currentTime = startTime;
             !currentTime.isAfter(masterSchedule.getEndTime().minusSeconds(serviceDuration.toSecondOfDay()));
             currentTime = currentTime.plusMinutes(30)) {

            final LocalTime finalCurrentTime = currentTime;
            boolean isTimeOccupied = orders.stream().anyMatch(order -> {
                if (order.getStatusOfRecord() != StatusOfRecord.REGISTERED) {
                    return false;
                }

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
