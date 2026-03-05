package team2.lmssystem.dto.request.respond;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LeaveApprovalResponseDTO {

    private String approverName;

    private Integer stage;

    private String status;

    private String comments;

    private LocalDateTime decisionDate;

}