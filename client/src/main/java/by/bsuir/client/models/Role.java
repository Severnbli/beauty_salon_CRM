package by.bsuir.client.models;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Role {
    Long id;
    String name;
    Integer accessLevel;
}