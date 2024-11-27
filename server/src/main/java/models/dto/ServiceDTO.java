package models.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.sql.Time;

@Builder
@Data
public class ServiceDTO implements Serializable {
    private long id;
    private String name;
    private double price;
    private Time timeCost;
}
