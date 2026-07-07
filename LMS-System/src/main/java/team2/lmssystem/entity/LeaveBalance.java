package team2.lmssystem.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Tracks remaining leave days per employee per leave type.
 * One row per (user, leaveType) pair — enforced by the unique constraint.
 * Balance is only decremented on approval, never on submission.
 * Table: {@code leave_balances}
 */
@Entity
@Table(name = "leave_balances",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "leave_type_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    private int remainingDays;
}
