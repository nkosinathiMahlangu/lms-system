package team2.lmssystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/** Used for updating a leave type — only the name can be changed. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateLeaveTypeRequest {

    @NotBlank(message = "Leave type name is required")
    private String name;
}
