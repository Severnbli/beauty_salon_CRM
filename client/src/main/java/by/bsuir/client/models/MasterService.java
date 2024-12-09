package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

import java.util.logging.Logger;

@Data
@Builder
public class MasterService implements Cloneable {
    private static final Logger log = Logger.getLogger(MasterService.class.getName());

    private Long id;
    private Master master;
    private Service service;

    @Override
    public MasterService clone() {
        try {
            MasterService cloned = (MasterService) super.clone();
            if (master != null) cloned.master = master.clone();
            if (service != null) cloned.service = service.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            log.severe("Cloning failed: " + e);
            return null;
        }
    }
}
