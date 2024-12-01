package db.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String login;

    @Column(nullable = false, length = 32)
    private String password;

    @Column(name = "first_name", nullable = false, length = 40)
    private String firstName;

    @Column(name = "last_name", length = 40)
    private String lastName;

    @Column(length = 80)
    private String email;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}
