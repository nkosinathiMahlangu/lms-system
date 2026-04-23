package team2.lmssystem.service.leaveservice;

import team2.lmssystem.dto.request.ApplyLeaveRequest;
import team2.lmssystem.dto.request.LeaveActionRequest;
import team2.lmssystem.dto.respond.LeaveResponse;

import java.util.List;

public interface LeaveService {

    String applyLeave(String username, ApplyLeaveRequest request);

    String approveOrRejectLeave(String adminUsername, LeaveActionRequest request);

    List<LeaveResponse> getUserLeaves(String username);
}
