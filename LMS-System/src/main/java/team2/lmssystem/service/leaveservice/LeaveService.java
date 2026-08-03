package team2.lmssystem.service.leaveservice;

import team2.lmssystem.dto.request.ApplyLeaveRequest;
import team2.lmssystem.dto.request.LeaveActionRequest;
import team2.lmssystem.dto.request.UpdateLeaveTypeRequest;
import team2.lmssystem.dto.respond.LeaveBalanceResponse;
import team2.lmssystem.dto.respond.LeaveResponse;
import team2.lmssystem.dto.respond.LeaveTypeResponse;

import java.util.List;

public interface LeaveService {

    // ---- Employee ----
    String applyLeave(String username, ApplyLeaveRequest request);
    String cancelLeave(String username, Long leaveRequestId);
    List<LeaveResponse> getUserLeaves(String username);
    List<LeaveBalanceResponse> getLeaveBalances(String username);

    // ---- Admin — leave requests ----
    String approveOrRejectLeave(String adminUsername, LeaveActionRequest request);
    List<LeaveResponse> getAllLeaveRequests(String status);

    // ---- Admin — leave type management ----
    LeaveTypeResponse updateLeaveType(Long id, UpdateLeaveTypeRequest request);
    String deleteLeaveType(Long id);
    List<LeaveTypeResponse> getAllLeaveTypes();
}
