package team2.lmssystem.dto.request;


import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveRequestDTO {

    private Long leaveTypeId;

    private LocalDate startDate;

    private LocalDate endDate;

    private String reason;

}
