package team2.lmssystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

/**
 * Represents a system user — either an ADMIN or an EMPLOYEE.
 * Table: {@code users}
 */
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    // Soft-disable without deleting the account
    private boolean enabled = true;

    // Fetched EAGERLY so Spring Security can read authorities
    // outside of an active Hibernate session (e.g. inside JwtFilter)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;
}
