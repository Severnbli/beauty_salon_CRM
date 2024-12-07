package by.bsuir.server.services;

import by.bsuir.server.db.dao.MasterScheduleDAO;
import by.bsuir.server.db.entities.DayOfWeek;
import by.bsuir.server.db.entities.Master;
import by.bsuir.server.db.entities.MasterSchedule;
import by.bsuir.server.utils.Nullifable;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public void nullify() {
        masterScheduleDAO = null;
        gson = null;

        System.gc();
    }
}
