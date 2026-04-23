package team2.lmssystem.entity;

import jakarta.persistence.*;
import lombok.*;
import team2.lmssystem.enums.LeaveStatus;

import java.time.LocalDate;

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

    // Who applied
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Type of leave
    @ManyToOne
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    private LocalDate startDate;
    private LocalDate endDate;

    private int numberOfDays;

    private String reason;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    // Admin who approved/rejected
    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;
}