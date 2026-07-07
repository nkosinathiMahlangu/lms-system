package team2.lmssystem.dto.respond;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Response payload representing a single leave request.
 *
 * <p>Returned by:</p>
 * <ul>
 *   <li>{@code GET /employee/leave} — employee views their own leave history</li>
 * </ul>
 *
 * <p>Maps from a {@link team2.lmssystem.entity.LeaveRequest} entity.
 * Sensitive or internal fields (e.g. the approving admin's details,
 * the internal user ID) are intentionally excluded.</p>
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
public class LeaveResponse {

    /** The database ID of the leave request. */
    private Long id;

    /** The name of the leave category (e.g. "Annual Leave", "Sick Leave"). */
    private String leaveType;

    /** The first day of the leave period (inclusive). */
    private LocalDate startDate;

    /** The last day of the leave period (inclusive). */
    private LocalDate endDate;

    /** Total number of calendar days covered by the request (endDate - startDate + 1). */
    private int numberOfDays;

    /**
     * The current status of the request as a string.
     * One of: {@code "PENDING"}, {@code "APPROVED"}, {@code "REJECTED"}.
     * See {@link team2.lmssystem.enums.LeaveStatus}.
     */
    private String status;
}
