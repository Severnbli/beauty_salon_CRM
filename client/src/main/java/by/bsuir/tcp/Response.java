package by.bsuir.tcp;

import by.bsuir.enums.ResponseStatus;
import lombok.Data;

@Data
public class Response {
    private ResponseStatus status;
    private String message;
    private String data;
}
