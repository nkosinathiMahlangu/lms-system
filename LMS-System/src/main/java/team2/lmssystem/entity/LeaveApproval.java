package team2.lmssystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long approvalId;

    @ManyToOne
    private LeaveApplication leaveApplication;

    @ManyToOne
    private User approver;

    private Integer stage;

    private String status;

    private LocalDateTime decisionDate;

    private String comments;
}