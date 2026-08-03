package team2.lmssystem.dto.respond;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/** Shows an employee how many days they have left for a specific leave type. */
@Getter
@Setter
@AllArgsConstructor
@Builder
public class LeaveBalanceResponse {

    private String leaveType;
    private int remainingDays;
}
