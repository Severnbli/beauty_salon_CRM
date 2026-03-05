package by.bsuir.server.db.entities;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public enum DayOfWeek {
    MONDAY("Понедельник"),
    TUESDAY("Вторник"),
    WEDNESDAY("Среда"),
    THURSDAY("Четверг"),
    FRIDAY("Пятница"),
    SATURDAY("Суббота"),
    SUNDAY("Воскресенье");

    private final String russianName;

    DayOfWeek(String russianName) {
        this.russianName = russianName;
    }

    public static DayOfWeek getDayOfWeekByDate(LocalDate date) {
        return switch (date.getDayOfWeek()) {
            case java.time.DayOfWeek.TUESDAY -> DayOfWeek.TUESDAY;
            case java.time.DayOfWeek.WEDNESDAY -> DayOfWeek.WEDNESDAY;
            case java.time.DayOfWeek.THURSDAY -> DayOfWeek.THURSDAY;
            case java.time.DayOfWeek.FRIDAY -> DayOfWeek.FRIDAY;
            case java.time.DayOfWeek.SATURDAY -> DayOfWeek.SATURDAY;
            case java.time.DayOfWeek.SUNDAY -> DayOfWeek.SUNDAY;
            default -> DayOfWeek.MONDAY;
        };
    }
}
