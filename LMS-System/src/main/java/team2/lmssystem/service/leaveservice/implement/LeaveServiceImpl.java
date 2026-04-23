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
            throw new RuntimeException("End date cannot be before start date");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new RuntimeException("Leave type not found"));

        long days = ChronoUnit.DAYS.between(
                request.getStartDate(),
                request.getEndDate()
        ) + 1;

        LeaveBalance balance = leaveBalanceRepository
                .findByUserAndLeaveType(user, leaveType)
                .orElseThrow(() -> new RuntimeException("Leave balance not found"));

        if (balance.getRemainingDays() < days) {
            return "Insufficient leave balance";
        }

        LeaveRequest leaveRequest = LeaveRequest.builder()
                .user(user)
                .leaveType(leaveType)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .numberOfDays((int) days)
                .reason(request.getReason())
                .status(LeaveStatus.PENDING)
                .build();

        leaveRequestRepository.save(leaveRequest);

        return "Leave applied successfully";
    }

    @Override
    public String approveOrRejectLeave(String adminUsername, LeaveActionRequest request) {

        LeaveRequest leaveRequest = leaveRequestRepository.findById(request.getLeaveRequestId())
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Leave already processed");
        }

        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (request.getApproved()) {

            LeaveBalance balance = leaveBalanceRepository
                    .findByUserAndLeaveType(
                            leaveRequest.getUser(),
                            leaveRequest.getLeaveType()
                    ).orElseThrow(() -> new RuntimeException("Leave balance not found"));

            balance.setRemainingDays(
                    balance.getRemainingDays() - leaveRequest.getNumberOfDays()
            );

            leaveBalanceRepository.save(balance);

            leaveRequest.setStatus(LeaveStatus.APPROVED);

        } else {
            leaveRequest.setStatus(LeaveStatus.REJECTED);
        }

        leaveRequest.setApprovedBy(admin);
        leaveRequestRepository.save(leaveRequest);

        return "Leave processed successfully";
    }

    @Override
    public List<LeaveResponse> getUserLeaves(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return leaveRequestRepository.findByUser(user)
                .stream()
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
