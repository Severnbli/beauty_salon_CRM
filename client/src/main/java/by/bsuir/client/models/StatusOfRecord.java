package by.bsuir.client.models;

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

    @Override
    public String toString() {
        return russianName;
    }
}
