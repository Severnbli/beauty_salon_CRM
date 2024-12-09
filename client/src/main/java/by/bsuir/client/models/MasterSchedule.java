package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;
import java.util.logging.Logger;

@Data
@Builder
public class MasterSchedule implements Cloneable {
    private static final Logger log = Logger.getLogger(MasterSchedule.class.getName());

    private Long id;
    private Master master;
    private by.bsuir.client.models.DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    @Override
    public MasterSchedule clone() {
        try {
            MasterSchedule cloned = (MasterSchedule) super.clone();
            if (master != null) cloned.master = master.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            log.severe("Cloning failed: " + e);
            return null;
        }
    }
}
