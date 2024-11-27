package models.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class ConsumableDTO implements Serializable {
    private long id;
    private String name;
    private long quantity;
}
