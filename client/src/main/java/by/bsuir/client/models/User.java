package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private Long id;
    private String login;
    private String password;
    private Role role;
    private PersonData personData;

    @Override
    public String toString() {
        return login;
    }
}