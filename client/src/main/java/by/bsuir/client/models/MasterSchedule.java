package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class MasterSchedule {
    Long id;
    Master master;
    DayOfWeek dayOfWeek;
    LocalTime startTime;
    LocalTime endTime;
}