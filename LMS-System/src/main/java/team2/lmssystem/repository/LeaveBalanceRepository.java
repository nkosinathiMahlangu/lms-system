package team2.lmssystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team2.lmssystem.entity.LeaveBalance;
import team2.lmssystem.entity.LeaveType;
import team2.lmssystem.entity.User;

import java.util.List;
import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    // Used for both balance checks (apply) and balance deductions (approve)
    Optional<LeaveBalance> findByUserAndLeaveType(User user, LeaveType leaveType);

    List<LeaveBalance> findByUser(User user);

    // Used when deleting a user — remove all their balances first
    void deleteByUser(User user);

    // Used when deleting a leave type — remove all balances for that type first
    void deleteByLeaveType(LeaveType leaveType);
}
