package team2.lmssystem.dto.respond;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaveResponse {

    private Long id;
    private String employeeName;    // shown on admin dashboard —
    private String username;        // needed for admin to identify the employee
    private String leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private int numberOfDays;
    private String reason;
    private String status;
    private String approvedBy;      // admin who actioned it — null while PENDING
}
