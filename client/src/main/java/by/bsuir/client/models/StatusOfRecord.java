package by.bsuir.client.models;

import lombok.Getter;

@Getter
public enum StatusOfRecord {
    REGISTERED("Зарегистрирован"),
    APPROVED("Утверждён"),
    REJECTED("Отменён"),
    EXECUTED("Осуществлён");

    private final String russianName;

    StatusOfRecord(String russianName) {
        this.russianName = russianName;
    }
}
