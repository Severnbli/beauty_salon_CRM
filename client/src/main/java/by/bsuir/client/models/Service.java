package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Time;

@Data
@Builder
public class Service {
    Long id;
    String name;
    BigDecimal price;
    Time timeCost;
}