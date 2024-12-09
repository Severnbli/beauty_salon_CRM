package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.logging.Logger;

@Data
@Builder
public class SecretCode implements Cloneable {
    private static final Logger log = Logger.getLogger(SecretCode.class.getName());

    private Long id;
    private String email;
    private String secretCode;
    private LocalDateTime timestampOfFormation;
    private LocalTime actionTime;

    @Override
    public SecretCode clone() {
        try {
            return (SecretCode) super.clone();
        } catch (CloneNotSupportedException e) {
            log.severe("Cloning failed: " + e);
            return null;
        }
    }
}
