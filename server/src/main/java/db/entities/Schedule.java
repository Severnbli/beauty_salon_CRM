package db.entities;

import enums.DayOfWeek;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Time;

@Entity
@Table(name = "masters_schedules")
@Data
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "master_id", nullable = false)
    private Master master;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private Time startTime;

    @Column(name = "end_time", nullable = false)
    private Time endTime;
}
