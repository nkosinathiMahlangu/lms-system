package team2.lmssystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team2.lmssystem.entity.LeaveBalance;
import team2.lmssystem.entity.LeaveType;
import team2.lmssystem.entity.User;

import java.util.Optional;
import java.util.List;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    Optional<LeaveBalance> findByUserAndLeaveType(User user, LeaveType leaveType);

    List<LeaveBalance> findByUser(User user);
}