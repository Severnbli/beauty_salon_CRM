package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    Long id;
    String login;
    String password;
    Role role;
    PersonData personData;
}