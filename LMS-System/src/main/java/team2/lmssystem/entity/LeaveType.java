package team2.lmssystem.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Category of leave an employee can apply for (e.g. Annual Leave, Sick Leave).
 * {@code defaultDays} is the annual quota used when seeding a new employee's balance.
 * Table: {@code leave_types}
 */
@Entity
@Table(name = "leave_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private int defaultDays;
}
