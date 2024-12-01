package db.entities;

import db.compoundIdentifiers.MasterServiceId;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "masters_services")
@Data
@IdClass(MasterServiceId.class)
public class MasterService {
    @Id
    @ManyToOne
    @JoinColumn(name = "master_id", nullable = false)
    private Master master;

    @Id
    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;
}

