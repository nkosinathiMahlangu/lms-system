package team2.lmssystem.service.leaveservice.implement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.lmssystem.dto.request.ApplyLeaveRequest;
import team2.lmssystem.dto.request.LeaveActionRequest;
import team2.lmssystem.dto.request.UpdateLeaveTypeRequest;
import team2.lmssystem.dto.respond.LeaveBalanceResponse;
import team2.lmssystem.dto.respond.LeaveResponse;
import team2.lmssystem.dto.respond.LeaveTypeResponse;
import team2.lmssystem.entity.LeaveBalance;
import team2.lmssystem.entity.LeaveRequest;
import team2.lmssystem.entity.LeaveType;
import team2.lmssystem.entity.User;
import team2.lmssystem.enums.LeaveStatus;
import team2.lmssystem.exception.BadRequestException;
import team2.lmssystem.exception.DuplicateResourceException;
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

    // -------------------------------------------------------------------------
    // Employee — apply leave
    // -------------------------------------------------------------------------

    @Override
    public String applyLeave(String username, ApplyLeaveRequest request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date cannot be before start date");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("LeaveType", "id", request.getLeaveTypeId()));

        // Block duplicate pending request for the same leave type
        if (leaveRequestRepository.existsByUserAndLeaveTypeAndStatus(user, leaveType, LeaveStatus.PENDING)) {
            throw new BadRequestException(
                    "You already have a pending " + leaveType.getName()
                            + " request. Cancel it first or wait for it to be actioned.");
        }

        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;

        LeaveBalance balance = leaveBalanceRepository
                .findByUserAndLeaveType(user, leaveType)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "LeaveBalance", "user + leaveType", username + " / " + leaveType.getName()));

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

    // -------------------------------------------------------------------------
    // Employee — cancel leave
    // -------------------------------------------------------------------------

    @Override
    public String cancelLeave(String username, Long leaveRequestId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveRequest", "id", leaveRequestId));

        if (!leaveRequest.getUser().getUsername().equals(username)) {
            throw new BadRequestException("You can only cancel your own leave requests");
        }

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException(
                    "Only pending leave requests can be cancelled. This request is already "
                            + leaveRequest.getStatus().name().toLowerCase());
        }

        leaveRequestRepository.delete(leaveRequest);
        return "Leave request cancelled successfully";
    }

    // -------------------------------------------------------------------------
    // Employee — view own leave history
    // -------------------------------------------------------------------------

    @Override
    public List<LeaveResponse> getUserLeaves(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return leaveRequestRepository.findByUser(user).stream()
                .map(this::toLeaveResponse)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Employee — view leave balances
    // -------------------------------------------------------------------------

    @Override
    public List<LeaveBalanceResponse> getLeaveBalances(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return leaveBalanceRepository.findByUser(user).stream()
                .map(balance -> LeaveBalanceResponse.builder()
                        .leaveType(balance.getLeaveType().getName())
                        .remainingDays(balance.getRemainingDays())
                        .build())
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Admin — approve or reject leave request
    // -------------------------------------------------------------------------

    @Override
    public String approveOrRejectLeave(String adminUsername, LeaveActionRequest request) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(request.getLeaveRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("LeaveRequest", "id", request.getLeaveRequestId()));

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException(
                    "Leave request has already been "
                            + leaveRequest.getStatus().name().toLowerCase()
                            + " and cannot be processed again");
        }

        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", adminUsername));

        if (request.getApproved()) {
            LeaveBalance balance = leaveBalanceRepository
                    .findByUserAndLeaveType(leaveRequest.getUser(), leaveRequest.getLeaveType())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "LeaveBalance", "user + leaveType",
                            leaveRequest.getUser().getUsername() + " / " + leaveRequest.getLeaveType().getName()));

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

    // -------------------------------------------------------------------------
    // Admin — get all leave requests (optional status filter)
    // -------------------------------------------------------------------------

    @Override
    public List<LeaveResponse> getAllLeaveRequests(String status) {
        List<LeaveRequest> requests;

        if (status != null && !status.isBlank()) {
            // Filter by status if provided — e.g. ?status=PENDING for dashboard
            LeaveStatus leaveStatus;
            try {
                leaveStatus = LeaveStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid status value. Use PENDING, APPROVED, or REJECTED.");
            }
            requests = leaveRequestRepository.findByStatus(leaveStatus);
        } else {
            // No filter — return everything for the full admin dashboard view
            requests = leaveRequestRepository.findAll();
        }

        return requests.stream()
                .map(this::toLeaveResponse)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Admin — get all leave types (for dropdowns on the frontend)
    // -------------------------------------------------------------------------

    @Override
    public List<LeaveTypeResponse> getAllLeaveTypes() {
        return leaveTypeRepository.findAll().stream()
                .map(this::toLeaveTypeResponse)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Admin — leave type management
    // -------------------------------------------------------------------------

    @Override
    public LeaveTypeResponse updateLeaveType(Long id, UpdateLeaveTypeRequest request) {
        LeaveType leaveType = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveType", "id", id));

        // Only the name is editable — defaultDays stays as originally set
        if (!leaveType.getName().equalsIgnoreCase(request.getName())
                && leaveTypeRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("LeaveType", "name", request.getName());
        }

        leaveType.setName(request.getName());

        return toLeaveTypeResponse(leaveTypeRepository.save(leaveType));
    }

    @Override
    @Transactional
    public String deleteLeaveType(Long id) {
        LeaveType leaveType = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveType", "id", id));

        // Block deletion if any employee currently has a PENDING request for this type —
        // deleting the type mid-flow would orphan those requests
        boolean hasPendingRequests = leaveRequestRepository
                .findByStatus(LeaveStatus.PENDING)
                .stream()
                .anyMatch(lr -> lr.getLeaveType().getId().equals(id));

        if (hasPendingRequests) {
            throw new BadRequestException(
                    "Cannot delete '" + leaveType.getName()
                            + "' — there are pending leave requests for this type. "
                            + "Action them first (approve or reject) before deleting.");
        }

        // Remove all leave requests for this type (approved/rejected history) and
        // all employee balances — both reference leave_type_id via FK
        leaveRequestRepository.deleteByLeaveType(leaveType);
        leaveBalanceRepository.deleteByLeaveType(leaveType);

        leaveTypeRepository.delete(leaveType);
        return "Leave type '" + leaveType.getName() + "' deleted successfully";
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private LeaveResponse toLeaveResponse(LeaveRequest lr) {
        return LeaveResponse.builder()
                .id(lr.getId())
                .employeeName(lr.getUser().getFirstName() + " " + lr.getUser().getLastName())
                .username(lr.getUser().getUsername())
                .leaveType(lr.getLeaveType().getName())
                .startDate(lr.getStartDate())
                .endDate(lr.getEndDate())
                .numberOfDays(lr.getNumberOfDays())
                .reason(lr.getReason())
                .status(lr.getStatus().name())
                .approvedBy(lr.getApprovedBy() != null
                        ? lr.getApprovedBy().getFirstName() + " " + lr.getApprovedBy().getLastName()
                        : null)
                .build();
    }

    private LeaveTypeResponse toLeaveTypeResponse(LeaveType lt) {
        return LeaveTypeResponse.builder()
                .id(lt.getId())
                .name(lt.getName())
                .defaultDays(lt.getDefaultDays())
                .build();
    }
}
