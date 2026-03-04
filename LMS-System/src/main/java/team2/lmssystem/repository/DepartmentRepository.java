package team2.lmssystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team2.lmssystem.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
