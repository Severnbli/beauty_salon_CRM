package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class Order {
    Long id;
    User client;
    Master master;
    Service service;
    Timestamp date;
    StatusOfRecord statusOfRecord;
}