package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceConsumable {
    private Long id;
    private Service service;
    private Consumable consumable;
}