package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

import java.util.logging.Logger;

@Data
@Builder
public class Master implements Cloneable {
    private static final Logger log = Logger.getLogger(Master.class.getName());

    private Long id;
    private User user;
    private String note;
    private String grade;

    @Override
    public String toString() {
        return user.getPersonData().getFirstName();
    }

    @Override
    public Master clone() {
        try {
            Master cloned = (Master) super.clone();
            if (user != null) cloned.user = user.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            log.severe("Cloning failed: " + e);
            return null;
        }
    }
}
