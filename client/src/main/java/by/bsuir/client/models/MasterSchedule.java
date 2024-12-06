package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class MasterSchedule {
    private Long id;
    private Master master;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
}