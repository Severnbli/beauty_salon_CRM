package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceConsumable {
    Long id;
    Service service;
    Consumable consumable;
}