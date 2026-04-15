package team2.lmssystem.entity;

import jakarta.persistence.*;
import lombok.*;
import team2.lmssystem.enums.Role;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User {
    //fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Integer leaveBalance;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private List<LeaveApplication> leaveApplications;
}
