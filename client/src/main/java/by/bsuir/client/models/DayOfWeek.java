package by.bsuir.client.models;

import lombok.Getter;

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
}
