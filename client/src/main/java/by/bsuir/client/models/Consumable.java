package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Consumable {
    Long id;
    String name;
    int quantity;
}