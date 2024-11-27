package models.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class UserDTO implements Serializable {
    private long id;
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private RoleDTO role;
}
