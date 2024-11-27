package models;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Builder
@Data
public class Order implements Serializable {
    private long id;
    private Timestamp date;
    private String status;
    private long masterId;
    private long clientId;
    private long serviceId;
}
