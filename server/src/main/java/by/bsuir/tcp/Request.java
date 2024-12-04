package by.bsuir.tcp;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Request implements Serializable {
    private RequestType type;
    private String message;
    private String data;
}
