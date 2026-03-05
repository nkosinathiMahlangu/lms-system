package team2.lmssystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team2.lmssystem.entity.LeaveApproval;

import java.util.List;

public interface LeaveApprovalRepository extends JpaRepository<LeaveApproval, Long> {
    List<LeaveApproval> findByApproverUserId(Long userId);
}
