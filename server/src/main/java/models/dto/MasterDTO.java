package models.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class MasterDTO implements Serializable {
    private long id;
    private String note;
}
