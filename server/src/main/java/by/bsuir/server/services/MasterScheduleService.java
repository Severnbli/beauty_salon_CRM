package by.bsuir.server.services;

import by.bsuir.server.db.dao.MasterScheduleDAO;
import by.bsuir.server.db.entities.DayOfWeek;
import by.bsuir.server.db.entities.Master;
import by.bsuir.server.db.entities.MasterSchedule;
import by.bsuir.server.utils.Nullifable;
import by.bsuir.tcp.Request;
import by.bsuir.tcp.Response;
import by.bsuir.tcp.ResponseStatus;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MasterScheduleService implements Nullifable {
    private MasterScheduleDAO masterScheduleDAO = new MasterScheduleDAO();
    private Gson gson = Converters.registerLocalTime(Converters.registerLocalDateTime(new GsonBuilder())).create();

    public List<MasterSchedule> getMastersSchedulesByMastersIdsAndDayOfWeek(List<Master> masters, DayOfWeek dayOfWeek) {
        List<MasterSchedule> mastersSchedules = new ArrayList<>();

        for (Master master : masters) {
            final MasterSchedule masterSchedule = masterScheduleDAO.getMasterScheduleTimeByDayOfWeek(
                    master.getId(),
                    dayOfWeek
            );

            if (masterSchedule != null) {
                mastersSchedules.add(masterSchedule);
            }
        }

        return mastersSchedules;
    }

    public Response getAllMasterSchedules() {
        return Response.builder()
                .status(ResponseStatus.OK)
                .message("Расписание мастеров успешно получено!")
                .data(gson.toJson(masterScheduleDAO.getAll()))
                .build();
    }

    public Response getMasterSchedulesByMaster(Request req) {
        Master master = gson.fromJson(req.getData(), Master.class);

        if (master == null) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Ошибка: данные о мастере не разобраны!")
                    .build();
        }

        return Response.builder()
                .status(ResponseStatus.OK)
                .data(gson.toJson(masterScheduleDAO.getMasterSchedulesByMasterId(master.getId())))
                .build();
    }

    public Response updateMasterSchedules(Request req) {
        Type listType = new TypeToken<List<MasterSchedule>>() {}.getType();
        List<MasterSchedule> masterSchedules = gson.fromJson(req.getData(), listType);

        for (MasterSchedule masterSchedule : masterSchedules) {
            masterScheduleDAO.update(masterSchedule);
        }

        return Response.builder()
                .status(ResponseStatus.OK)
                .build();
    }

    public Response setupMasterSchedulesByDefault(Request req) {
        Master master = gson.fromJson(req.getData(), Master.class);

        if (master == null) {
            return Response.builder()
                    .status(ResponseStatus.ERROR)
                    .message("Данные о мастере не получилось разобрать!")
                    .build();
        }

        List<MasterSchedule> masterSchedules = masterScheduleDAO.getMasterSchedulesByMasterId(master.getId());

        Set<DayOfWeek> existingDays = masterSchedules.stream()
                .map(MasterSchedule::getDayOfWeek)
                .collect(Collectors.toSet());

        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            if (!existingDays.contains(dayOfWeek)) {
                MasterSchedule masterSchedule = new MasterSchedule();
                masterSchedule.setDayOfWeek(dayOfWeek);
                masterSchedule.setMaster(master);
                masterSchedule.setStartTime(LocalTime.of(9, 0));
                masterSchedule.setEndTime(LocalTime.of(18, 0));
                masterScheduleDAO.save(masterSchedule);
            }
        }

        return Response.builder()
                .status(ResponseStatus.OK)
                .build();
    }

    @Override
    public void nullify() {
        masterScheduleDAO = null;
        gson = null;

        System.gc();
    }
}
