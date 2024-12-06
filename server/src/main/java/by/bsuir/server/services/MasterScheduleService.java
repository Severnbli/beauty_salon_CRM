package by.bsuir.server.services;

import by.bsuir.server.db.dao.MasterScheduleDAO;
import by.bsuir.server.db.entities.DayOfWeek;
import by.bsuir.server.db.entities.Master;
import by.bsuir.server.db.entities.MasterSchedule;
import by.bsuir.server.utils.Nullifable;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MasterScheduleService implements Nullifable {
    private MasterScheduleDAO masterScheduleDAO = new MasterScheduleDAO();
    private Gson gson = new Gson();

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
