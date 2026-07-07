package team2.lmssystem.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for the {@code PUT /admin/leave/action} endpoint.
 *
 * <p>An admin uses this DTO to approve or reject a pending leave request.
 * Only {@code PENDING} requests can be acted on — attempting to process
 * an already-approved or rejected request will result in a
 * {@link team2.lmssystem.exception.BadRequestException}.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveActionRequest {

    /**
     * The ID of the {@link team2.lmssystem.entity.LeaveRequest} to act on.
     * Must not be null.
     */
    @NotNull(message = "Leave request ID is required")
    private Long leaveRequestId;

    /**
     * The admin's decision:
     * <ul>
     *   <li>{@code true}  → approve the request (balance deducted)</li>
     *   <li>{@code false} → reject the request (balance unchanged)</li>
     * </ul>
     * Must not be null.
     */
    @NotNull(message = "Approval decision is required (true = approve, false = reject)")
    private Boolean approved;
}
