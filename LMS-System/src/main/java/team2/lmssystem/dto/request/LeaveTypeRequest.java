package team2.lmssystem.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/** Used for creating a leave type (name + defaultDays). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveTypeRequest {

    @NotBlank(message = "Leave type name is required")
    private String name;

    @NotNull(message = "Default days is required")
    @Min(value = 0, message = "Default days cannot be negative")
    private Integer defaultDays;
}
