package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonData {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    @Override
    public String toString() {
        return firstName;
    }
}