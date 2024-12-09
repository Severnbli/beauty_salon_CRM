package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

import java.util.logging.Logger;

@Data
@Builder
public class User implements Cloneable {
    private static final Logger log = Logger.getLogger(User.class.getName());

    private Long id;
    private String login;
    private String password;
    private Role role;
    private PersonData personData;
    private Boolean isDoubleEntry;

    @Override
    public String toString() {
        return login;
    }

    @Override
    public User clone() {
        try {
            User cloned = (User) super.clone();

            if (this.role != null) {
                cloned.role = this.role.clone();
            }
            if (this.personData != null) {
                cloned.personData = this.personData.clone();
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            log.severe("Cloning failed: " + e);
            return null;
        }
    }
}
