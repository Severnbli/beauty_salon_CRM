package by.bsuir.server.db.entities;

import lombok.Getter;

@Getter
public enum StatusOfRecord {
    REGISTERED("Зарегистрирован"),
    REJECTED("Отменён"),
    EXECUTED("Осуществлён");

    private final String russianName;

    StatusOfRecord(String russianName) {
        this.russianName = russianName;
    }
}
