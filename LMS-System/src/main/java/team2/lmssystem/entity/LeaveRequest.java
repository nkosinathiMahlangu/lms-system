package team2.lmssystem.entity;

import jakarta.persistence.*;
import lombok.*;
import team2.lmssystem.enums.LeaveStatus;

import java.time.LocalDate;

/**
 * A single leave application submitted by an employee.
 * Lifecycle: PENDING → APPROVED (balance deducted) or REJECTED (balance unchanged).
 * Table: {@code leave_requests}
 */
@Entity
@Table(name = "leave_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    private LocalDate startDate;
    private LocalDate endDate;

    // Stored for quick access — calculated as (endDate - startDate + 1)
    private int numberOfDays;

    private String reason;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    // Null while PENDING; set when an admin approves or rejects
    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;
}
