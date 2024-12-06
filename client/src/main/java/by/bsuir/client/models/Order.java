package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Order {
    Long id;
    User client;
    Master master;
    Service service;
    LocalDateTime date;
    StatusOfRecord statusOfRecord;
}