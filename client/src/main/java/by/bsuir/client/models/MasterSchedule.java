package by.bsuir.client.models;

import java.sql.Time;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MasterSchedule {
    Long id;
    Master master;
    DayOfWeek dayOfWeek;
    Time startTime;
    Time endTime;
}