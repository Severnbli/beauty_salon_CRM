package models.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class RoleDTO implements Serializable {
    private long id;
    private String name;
}
