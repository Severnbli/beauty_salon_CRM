package client.tcp;

import client.enums.RequestType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request implements Serializable {
    private RequestType type;
    private String message;
    private String data;
}
