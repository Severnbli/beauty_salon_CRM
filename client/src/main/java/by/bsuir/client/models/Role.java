package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

import java.util.logging.Logger;

@Data
@Builder
public class Role implements Cloneable {
    private static final Logger log = Logger.getLogger(Role.class.getName());

    private Long id;
    private String name;
    private Integer accessLevel;

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Role clone() {
        try {
            return (Role) super.clone();
        } catch (CloneNotSupportedException e) {
            log.severe("Cloning failed: " + e);
            return null;
        }
    }
}
