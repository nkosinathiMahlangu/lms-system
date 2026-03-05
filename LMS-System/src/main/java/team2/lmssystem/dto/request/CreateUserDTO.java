package team2.lmssystem.dto.request;

import lombok.Data;

@Data
public class CreateUserDTO {

    private String name;

    private String email;

    private String password;

    private String role;

    private Long departmentId;

    private Long managerId;

}
