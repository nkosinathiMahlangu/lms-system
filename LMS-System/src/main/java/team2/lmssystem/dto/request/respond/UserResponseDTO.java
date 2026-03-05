package team2.lmssystem.dto.request.respond;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDTO {

    private Long userId;

    private String name;

    private String email;

    private String role;

    private String department;

    private String manager;

}