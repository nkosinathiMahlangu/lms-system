package team2.lmssystem.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.lmssystem.dto.request.ApprovalRequestDTO;
import team2.lmssystem.dto.request.LeaveRequestDTO;
import team2.lmssystem.dto.request.respond.LeaveResponseDTO;
import team2.lmssystem.entity.LeaveApplication;
import team2.lmssystem.entity.LeaveApproval;
import team2.lmssystem.entity.LeaveType;
import team2.lmssystem.entity.User;
import team2.lmssystem.repository.LeaveApplicationRepository;
import team2.lmssystem.repository.LeaveApprovalRepository;
import team2.lmssystem.repository.LeaveTypeRepository;
import team2.lmssystem.repository.UserRepository;
import team2.lmssystem.service.LeaveService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {
    //fields
    private final LeaveApplicationRepository leaveRepository;
    private final LeaveApprovalRepository approvalRepository;
    private final UserRepository userRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    @Override
    public LeaveResponseDTO applyLeave(Long employeeId, LeaveRequestDTO request) {

        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new RuntimeException("Leave type not found"));

        LeaveApplication leave = LeaveApplication.builder()
                .employee(employee)
                .leaveType(leaveType)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status("PENDING_MANAGER")
                .build();

        leaveRepository.save(leave);

        // Create first approval stage (Manager)

        LeaveApproval approval = LeaveApproval.builder()
                .leaveApplication(leave)
                .approver(employee.getManager())
                .stage(1)
                .status("PENDING")
                .build();

        approvalRepository.save(approval);

        return mapToResponse(leave);
    }

    @Override
    public LeaveResponseDTO managerApproval(Long managerId, ApprovalRequestDTO request) {

        LeaveApplication leave = leaveRepository.findById(request.getLeaveId())
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        LeaveApproval approval = approvalRepository
                .findByApproverUserId(managerId)
                .stream()
                .filter(a -> a.getLeaveApplication().getLeaveId().equals(request.getLeaveId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Approval not found"));

        approval.setStatus(request.getDecision());
        approval.setComments(request.getComments());
        approval.setDecisionDate(LocalDateTime.now());

        approvalRepository.save(approval);

        if ("APPROVED".equals(request.getDecision())) {

            leave.setStatus("PENDING_HR");

            // create HR approval stage

            LeaveApproval hrApproval = LeaveApproval.builder()
                    .leaveApplication(leave)
                    .stage(2)
                    .status("PENDING")
                    .build();

            approvalRepository.save(hrApproval);

        } else {

            leave.setStatus("REJECTED");

        }

        leaveRepository.save(leave);

        return mapToResponse(leave);
    }

    @Override
    public LeaveResponseDTO hrApproval(Long hrId, ApprovalRequestDTO request) {

        LeaveApplication leave = leaveRepository.findById(request.getLeaveId())
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        LeaveApproval approval = approvalRepository
                .findByApproverUserId(hrId)
                .stream()
                .filter(a -> a.getLeaveApplication().getLeaveId().equals(request.getLeaveId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Approval not found"));

        approval.setStatus(request.getDecision());
        approval.setComments(request.getComments());
        approval.setDecisionDate(LocalDateTime.now());

        approvalRepository.save(approval);

        if ("APPROVED".equals(request.getDecision())) {

            leave.setStatus("APPROVED");

            User employee = leave.getEmployee();

            int days = leave.getTotalDays();

            employee.setLeaveBalance(employee.getLeaveBalance() - days);

            userRepository.save(employee);

        } else {

            leave.setStatus("REJECTED");

        }

        leaveRepository.save(leave);

        return mapToResponse(leave);
    }

    //instatiaon obj
    private LeaveResponseDTO mapToResponse(LeaveApplication leave) {

        return LeaveResponseDTO.builder()
                .leaveId(leave.getLeaveId())
                .employeeName(leave.getEmployee().getName())
                .leaveType(leave.getLeaveType().getName())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .totalDays(leave.getTotalDays())
                .status(leave.getStatus())
                .build();
    }
}
