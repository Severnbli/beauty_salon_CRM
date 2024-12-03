package by.bsuir.tcp;

import lombok.Data;

import java.io.Serializable;

@Data
public class Request implements Serializable {
    private RequestType type;
    private String message;
    private String data;
}
