package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MasterService {
    private Long id;
    private Master master;
    private Service service;
}