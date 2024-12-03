package by.bsuir.server.db.entities.compoundIdentifiers;

import java.io.Serializable;
import lombok.Data;

@Data
public class ServiceConsumableId implements Serializable {
    private Long service;
    private Long consumable;
}

