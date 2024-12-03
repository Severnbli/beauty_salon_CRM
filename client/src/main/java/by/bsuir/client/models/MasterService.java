package by.bsuir.client.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MasterService {
    Long id;
    Master master;
    Service service;
}