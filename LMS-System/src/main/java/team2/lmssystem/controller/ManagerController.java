package team2.lmssystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.lmssystem.dto.request.ApprovalRequestDTO;
import team2.lmssystem.dto.request.respond.LeaveResponseDTO;
import team2.lmssystem.service.LeaveService;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final LeaveService leaveService;

    @PostMapping("/approve/{managerId}")
    public ResponseEntity<LeaveResponseDTO> approveLeave(
            @PathVariable Long managerId,
            @RequestBody ApprovalRequestDTO request) {

        LeaveResponseDTO response =
                leaveService.managerApproval(managerId, request);

        return ResponseEntity.ok(response);
    }
}
