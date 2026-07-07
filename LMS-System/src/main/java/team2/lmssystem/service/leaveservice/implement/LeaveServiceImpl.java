package team2.lmssystem.service.leaveservice.implement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.lmssystem.dto.request.ApplyLeaveRequest;
import team2.lmssystem.dto.request.LeaveActionRequest;
import team2.lmssystem.dto.respond.LeaveResponse;
import team2.lmssystem.entity.LeaveBalance;
import team2.lmssystem.entity.LeaveRequest;
import team2.lmssystem.entity.LeaveType;
import team2.lmssystem.entity.User;
import team2.lmssystem.enums.LeaveStatus;
import team2.lmssystem.exception.BadRequestException;
import team2.lmssystem.exception.ResourceNotFoundException;
import team2.lmssystem.repository.LeaveBalanceRepository;
import team2.lmssystem.repository.LeaveRequestRepository;
import team2.lmssystem.repository.LeaveTypeRepository;
import team2.lmssystem.repository.UserRepository;
import team2.lmssystem.service.leaveservice.LeaveService;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {

    private final UserRepository userRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;

    @Override
    public String applyLeave(String username, ApplyLeaveRequest request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date cannot be before start date");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "LeaveType", "id", request.getLeaveTypeId()));

        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;

        LeaveBalance balance = leaveBalanceRepository
                .findByUserAndLeaveType(user, leaveType)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "LeaveBalance", "user + leaveType",
                        username + " / " + leaveType.getName()));

        if (balance.getRemainingDays() < days) {
            throw new BadRequestException(
                    "Insufficient leave balance. Requested: " + days
                            + " day(s), Available: " + balance.getRemainingDays());
        }

        // Balance is NOT deducted here — only deducted when an admin approves
        leaveRequestRepository.save(LeaveRequest.builder()
                .user(user)
                .leaveType(leaveType)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .numberOfDays((int) days)
                .reason(request.getReason())
                .status(LeaveStatus.PENDING)
                .build());

        return "Leave applied successfully";
    }

    @Override
    public String approveOrRejectLeave(String adminUsername, LeaveActionRequest request) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(request.getLeaveRequestId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "LeaveRequest", "id", request.getLeaveRequestId()));

        // Guard against re-processing an already actioned request
        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException(
                    "Leave request has already been "
                            + leaveRequest.getStatus().name().toLowerCase()
                            + " and cannot be processed again");
        }

        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "username", adminUsername));

        if (request.getApproved()) {
            LeaveBalance balance = leaveBalanceRepository
                    .findByUserAndLeaveType(leaveRequest.getUser(), leaveRequest.getLeaveType())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "LeaveBalance", "user + leaveType",
                            leaveRequest.getUser().getUsername()
                                    + " / " + leaveRequest.getLeaveType().getName()));

            balance.setRemainingDays(balance.getRemainingDays() - leaveRequest.getNumberOfDays());
            leaveBalanceRepository.save(balance);
            leaveRequest.setStatus(LeaveStatus.APPROVED);
        } else {
            leaveRequest.setStatus(LeaveStatus.REJECTED);
        }

        leaveRequest.setApprovedBy(admin);
        leaveRequestRepository.save(leaveRequest);

        return "Leave " + leaveRequest.getStatus().name().toLowerCase() + " successfully";
    }

    @Override
    public List<LeaveResponse> getUserLeaves(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return leaveRequestRepository.findByUser(user).stream()
                .map(lr -> LeaveResponse.builder()
                        .id(lr.getId())
                        .leaveType(lr.getLeaveType().getName())
                        .startDate(lr.getStartDate())
                        .endDate(lr.getEndDate())
                        .numberOfDays(lr.getNumberOfDays())
                        .status(lr.getStatus().name())
                        .build())
                .collect(Collectors.toList());
    }
}
