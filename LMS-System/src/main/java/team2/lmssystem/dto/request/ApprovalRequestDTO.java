package team2.lmssystem.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ApprovalRequestDTO {

    private Long leaveId;

    private String decision;

    private String comments;

}