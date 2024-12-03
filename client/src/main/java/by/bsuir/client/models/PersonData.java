package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonData {
    Long id;
    String firstName;
    String lastName;
    String email;
}