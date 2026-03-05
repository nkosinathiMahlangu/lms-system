package team2.lmssystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team2.lmssystem.entity.LeaveApplication;

import java.util.List;

public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {
    List<LeaveApplication> findByEmployeeUserId(Long userId);
}
