package models.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.sql.Time;

@Builder
@Data
public class ScheduleDTO implements Serializable {
    private long id;
    private String dayOfWeek;
    private Time startTime;
    private Time endTime;
    private long masterId;
}
