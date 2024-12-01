package db.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "masters")
@Data
public class Master {
    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @Column(length = 400)
    private String note;
}

