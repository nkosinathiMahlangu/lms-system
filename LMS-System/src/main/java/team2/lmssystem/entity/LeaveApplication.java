package team2.lmssystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long leaveId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private User employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id")
    private LeaveType leaveType;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer totalDays;

    private String status;

    private String medicalProofFile;

    @OneToMany(mappedBy = "leaveApplication",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<LeaveApproval> approvals;
}