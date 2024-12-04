package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Master {
    Long id;
    User user;
    String note;
    String grade;
}