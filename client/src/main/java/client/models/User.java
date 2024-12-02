package client.models;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class User {
    Long id;
    String login;
    String password;
    String firstName;
    String lastName;
    String email;
    Role role;
}
