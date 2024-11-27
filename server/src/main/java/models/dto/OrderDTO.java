package models.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Builder
@Data
public class OrderDTO implements Serializable {
    private long id;
    private Timestamp date;
    private String status;
    private MasterDTO master;
    private UserDTO client;
    private ServiceDTO service;
}
