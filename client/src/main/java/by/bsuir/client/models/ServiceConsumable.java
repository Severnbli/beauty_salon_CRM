package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

import java.util.logging.Logger;

@Data
@Builder
public class ServiceConsumable implements Cloneable {
    private static final Logger log = Logger.getLogger(ServiceConsumable.class.getName());

    private Long id;
    private Service service;
    private Consumable consumable;

    @Override
    public ServiceConsumable clone() {
        try {
            ServiceConsumable cloned = (ServiceConsumable) super.clone();
            if (service != null) cloned.service = service.clone();
            if (consumable != null) cloned.consumable = consumable.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            log.severe("Cloning failed: " + e);
            return null;
        }
    }
}
