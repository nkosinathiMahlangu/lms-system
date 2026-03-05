package team2.lmssystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team2.lmssystem.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
