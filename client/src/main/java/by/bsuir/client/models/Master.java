package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Master {
    private Long id;
    private User user;
    private String note;
    private String grade;

    @Override
    public String toString() {
        return user.getPersonData().getFirstName();
    }
}