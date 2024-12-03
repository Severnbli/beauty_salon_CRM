package client.tcp;

import client.enums.ResponseStatus;
import lombok.Data;

@Data
public class Response {
    private ResponseStatus status;
    private String message;
    private String data;
}
