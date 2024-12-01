package db.entities;

import db.compoundIdentifiers.ServiceConsumableId;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "services_consumables")
@Data
@IdClass(ServiceConsumableId.class)
public class ServiceConsumable {
    @Id
    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Id
    @ManyToOne
    @JoinColumn(name = "consumable_id", nullable = false)
    private Consumable consumable;
}

