package by.bsuir.tcp;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Response implements Serializable {
    private ResponseStatus status;
    private String message;
    private String data;
}
