package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

import java.util.logging.Logger;

@Data
@Builder
public class Consumable implements Cloneable {
    private static final Logger log = Logger.getLogger(Consumable.class.getName());

    private Long id;
    private String name;
    private int quantity;

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Consumable clone() {
        try {
            return (Consumable) super.clone();
        } catch (CloneNotSupportedException e) {
            log.severe("Cloning failed: " + e);
            return null;
        }
    }
}
