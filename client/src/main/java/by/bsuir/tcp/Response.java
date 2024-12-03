package by.bsuir.tcp;

import by.bsuir.enums.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private ResponseStatus status;
    private String message;
    private String data;
}
