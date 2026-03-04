package team2.lmssystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "departments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Department {
    //fields
    @Id
    @GeneratedValue( strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "department_id")
    private Long id;

    @Column(name = "department_name")
    private String departmentName;
}
