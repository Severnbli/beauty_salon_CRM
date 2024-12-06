package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Consumable {
    private Long id;
    private String name;
    private int quantity;

    @Override
    public String toString() {
        return name;
    }
}