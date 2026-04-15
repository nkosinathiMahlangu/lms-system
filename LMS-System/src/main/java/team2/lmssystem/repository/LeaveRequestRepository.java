package team2.lmssystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team2.lmssystem.entity.LeaveRequest;
import team2.lmssystem.entity.User;
import team2.lmssystem.enums.LeaveStatus;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    // Employee view
    List<LeaveRequest> findByUser(User user);

    // Admin dashboard
    List<LeaveRequest> findByStatus(LeaveStatus status);

    // Combined filter
    List<LeaveRequest> findByUserAndStatus(User user, LeaveStatus status);
}