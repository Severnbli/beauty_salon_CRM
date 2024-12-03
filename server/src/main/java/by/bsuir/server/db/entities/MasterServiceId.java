package by.bsuir.server.db.entities;

import java.io.Serializable;
import lombok.Data;

@Data
public class MasterServiceId implements Serializable {
    private Long master;
    private Long service;
}

