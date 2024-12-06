package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Order {
    private Long id;
    private User client;
    private Master master;
    private Service service;
    private LocalDateTime date;
    private StatusOfRecord statusOfRecord;
}