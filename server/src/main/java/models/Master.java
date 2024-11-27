package models;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class Master implements Serializable {
    private long id;
    private String note;
}
