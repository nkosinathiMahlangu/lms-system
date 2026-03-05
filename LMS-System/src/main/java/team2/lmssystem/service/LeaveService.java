package team2.lmssystem.service;

import team2.lmssystem.dto.request.ApprovalRequestDTO;
import team2.lmssystem.dto.request.LeaveRequestDTO;
import team2.lmssystem.dto.request.respond.LeaveResponseDTO;

public interface LeaveService {

    LeaveResponseDTO applyLeave(Long employeeId, LeaveRequestDTO request);

    LeaveResponseDTO managerApproval(Long managerId, ApprovalRequestDTO request);

    LeaveResponseDTO hrApproval(Long hrId, ApprovalRequestDTO request);

}