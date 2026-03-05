package team2.lmssystem.dto.request.respond;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class LeaveResponseDTO {

    private Long leaveId;

    private String employeeName;

    private String leaveType;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer totalDays;

    private String status;

}
