package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.logging.Logger;

@Data
@Builder
public class Order implements Cloneable {
    private static final Logger log = Logger.getLogger(Order.class.getName());

    private Long id;
    private User client;
    private Master master;
    private Service service;
    private LocalDateTime date;
    private StatusOfRecord statusOfRecord;

    @Override
    public Order clone() {
        try {
            Order cloned = (Order) super.clone();
            if (client != null) cloned.client = client.clone();
            if (master != null) cloned.master = master.clone();
            if (service != null) cloned.service = service.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            log.severe("Cloning failed: " + e);
            return null;
        }
    }
}
