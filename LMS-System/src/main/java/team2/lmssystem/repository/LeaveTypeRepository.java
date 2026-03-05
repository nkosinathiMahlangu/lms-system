package team2.lmssystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team2.lmssystem.entity.LeaveType;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {
}
