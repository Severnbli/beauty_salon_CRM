package by.bsuir.client.models;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Role {
    private Long id;
    private String name;
    private Integer accessLevel;

    @Override
    public String toString() {
        return name;
    }
}