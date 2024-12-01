package db.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "consumables")
@Data
public class Consumable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String name;

    @Column(nullable = false)
    private int quantity;
}
