package team2.lmssystem.entity;

import jakarta.persistence.*;
import lombok.*;
import team2.lmssystem.enums.RoleName;

/**
 * Security role assigned to a {@link User} (ADMIN or EMPLOYEE).
 * Rows are pre-seeded — never created at runtime.
 * Table: {@code roles}
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private RoleName name;
}
