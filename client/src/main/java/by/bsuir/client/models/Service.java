package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
@Builder
public class Service {
    Long id;
    String name;
    BigDecimal price;
    LocalTime timeCost;
}