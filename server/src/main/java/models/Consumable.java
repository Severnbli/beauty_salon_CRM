package models;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class Consumable implements Serializable {
    private long id;
    private String name;
    private long quantity;
}
