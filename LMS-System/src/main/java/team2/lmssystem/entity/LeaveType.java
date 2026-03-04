package team2.lmssystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leave_types")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long leaveTypeId;

    @Column(name = "leave_type_name")
    private String name;

    private Integer maxDaysPerYear;

    private Boolean requiresProof;

}
