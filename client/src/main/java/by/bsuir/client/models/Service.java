package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.logging.Logger;

@Data
@Builder
public class Service implements Cloneable {
    private static final Logger log = Logger.getLogger(Service.class.getName());

    private Long id;
    private String name;
    private BigDecimal price;
    private LocalTime timeCost;

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Service clone() {
        try {
            return (Service) super.clone();
        } catch (CloneNotSupportedException e) {
            log.severe("Cloning failed: " + e);
            return null;
        }
    }
}
