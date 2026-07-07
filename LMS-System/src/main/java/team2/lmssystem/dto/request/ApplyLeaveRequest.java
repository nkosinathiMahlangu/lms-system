package team2.lmssystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

/**
 * Request body for the {@code POST /employee/leave/apply} endpoint.
 *
 * <p>An employee submits this DTO to request a period of leave.
 * The leave type is identified by its database ID rather than its name
 * to keep the contract stable if names are ever renamed.</p>
 *
 * <p>Date-range validation (end ≥ start) is enforced in
 * {@link team2.lmssystem.service.leaveservice.implement.LeaveServiceImpl}
 * rather than here, so the error message can include both date values.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplyLeaveRequest {

    /**
     * The ID of the {@link team2.lmssystem.entity.LeaveType} being requested
     * (e.g. the ID for "Annual Leave"). Must not be null.
     */
    @NotNull(message = "Leave type ID is required")
    private Long leaveTypeId;

    /** The first day of the requested leave period (inclusive). Must not be null. */
    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    /** The last day of the requested leave period (inclusive). Must not be null. */
    @NotNull(message = "End date is required")
    private LocalDate endDate;

    /** The employee's stated reason for taking leave. Must not be blank. */
    @NotBlank(message = "Reason is required")
    private String reason;
}
