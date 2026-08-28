package team2.lmssystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team2.lmssystem.entity.LeaveRequest;
import team2.lmssystem.entity.LeaveType;
import team2.lmssystem.entity.User;
import team2.lmssystem.enums.LeaveStatus;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByUser(User user);

    // Admin dashboard — typically used to list PENDING requests
    List<LeaveRequest> findByStatus(LeaveStatus status);

    List<LeaveRequest> findByUserAndStatus(User user, LeaveStatus status);

    // Used to block duplicate pending requests for the same leave type
    boolean existsByUserAndLeaveTypeAndStatus(User user, LeaveType leaveType, LeaveStatus status);

    // Used when deleting a user — remove all their requests first
    void deleteByUser(User user);

    // Used when deleting a leave type — remove all requests for that type first
    void deleteByLeaveType(LeaveType leaveType);
}
