package team2.lmssystem.service.leaveservice;

import team2.lmssystem.dto.request.ApplyLeaveRequest;
import team2.lmssystem.dto.request.LeaveActionRequest;
import team2.lmssystem.dto.respond.LeaveResponse;

import java.util.List;

/**
 * Contract for all leave-management operations in the LMS.
 *
 * <p>Implemented by
 * {@link team2.lmssystem.service.leaveservice.implement.LeaveServiceImpl}.</p>
 *
 * <p>Three operations are supported:</p>
 * <ol>
 *   <li><b>Apply for leave</b>    — employee submits a leave request</li>
 *   <li><b>Approve/reject leave</b> — admin acts on a pending request</li>
 *   <li><b>View own leaves</b>    — employee retrieves their leave history</li>
 * </ol>
 */
public interface LeaveService {

    /**
     * Submits a new leave request on behalf of the authenticated employee.
     *
     * <p>Validates date range and available balance before persisting
     * the request with status {@code PENDING}.</p>
     *
     * @param username the authenticated employee's username (from JWT principal)
     * @param request  the leave details: type, start date, end date, reason
     * @return a success message if the request was submitted
     * @throws team2.lmssystem.exception.BadRequestException
     *         if the end date is before the start date, or the employee has
     *         insufficient leave balance for the requested days
     * @throws team2.lmssystem.exception.ResourceNotFoundException
     *         if the user, leave type, or leave balance record is not found
     */
    String applyLeave(String username, ApplyLeaveRequest request);

    /**
     * Approves or rejects a pending leave request.
     *
     * <p>On approval, the employee's {@link team2.lmssystem.entity.LeaveBalance}
     * is decremented by the number of days in the request.
     * On rejection, the balance is left unchanged.</p>
     *
     * <p>In both cases the {@code approvedBy} field is set to the acting admin.</p>
     *
     * @param adminUsername the authenticated admin's username (from JWT principal)
     * @param request       contains the leave request ID and the approve/reject decision
     * @return a message confirming the action was applied
     * @throws team2.lmssystem.exception.ResourceNotFoundException
     *         if the leave request, admin user, or leave balance is not found
     * @throws team2.lmssystem.exception.BadRequestException
     *         if the leave request is not in {@code PENDING} status
     */
    String approveOrRejectLeave(String adminUsername, LeaveActionRequest request);

    /**
     * Returns all leave requests submitted by the authenticated employee.
     *
     * @param username the authenticated employee's username (from JWT principal)
     * @return list of the user's leave requests mapped to {@link LeaveResponse};
     *         empty list if the user has never submitted a request
     * @throws team2.lmssystem.exception.ResourceNotFoundException
     *         if no user with the given username exists
     */
    List<LeaveResponse> getUserLeaves(String username);
}
