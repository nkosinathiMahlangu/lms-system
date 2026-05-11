package team2.lmssystem.entity;

import jakarta.persistence.*;
import lombok.*;

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

    // Owning side
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Owning side
    @ManyToOne
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    private int remainingDays;
}
