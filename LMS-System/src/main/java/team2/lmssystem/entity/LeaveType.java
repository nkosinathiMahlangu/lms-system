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
    //fields
    @Id
    @GeneratedValue( strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name = "leave_type_name")
    private String leaveTypeName;
}
