package team2.lmssystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    @ManyToOne
    private User employee;

    @ManyToOne
    private LeaveType leaveType;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer totalDays;

    private String status;

    private String medicalProofFile;
}