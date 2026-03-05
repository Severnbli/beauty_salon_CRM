package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

import java.util.logging.Logger;

@Data
@Builder
public class PersonData implements Cloneable {
    private static final Logger log = Logger.getLogger(PersonData.class.getName());

    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    @Override
    public String toString() {
        return firstName;
    }

    @Override
    public PersonData clone() {
        try {
            return (PersonData) super.clone();
        } catch (CloneNotSupportedException e) {
            log.severe("Cloning failed: " + e);
            return null;
        }
    }
}
