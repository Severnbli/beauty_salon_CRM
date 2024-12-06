package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
@Builder
public class Service {
    private Long id;
    private String name;
    private BigDecimal price;
    private LocalTime timeCost;

    @Override
    public String toString() {
        return name;
    }
}