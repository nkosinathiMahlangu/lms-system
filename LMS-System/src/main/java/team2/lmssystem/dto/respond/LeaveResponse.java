package team2.lmssystem.dto.respond;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LeaveResponse {

    private Long id;
    private String leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private int numberOfDays;
    private String status;
}