package team2.lmssystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.lmssystem.dto.request.LeaveRequestDTO;
import team2.lmssystem.dto.request.respond.LeaveResponseDTO;
import team2.lmssystem.service.LeaveService;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @PostMapping("/apply/{employeeId}")
    public ResponseEntity<LeaveResponseDTO> applyLeave(
            @PathVariable Long employeeId,
            @RequestBody LeaveRequestDTO request) {

        LeaveResponseDTO response = leaveService.applyLeave(employeeId, request);

        return ResponseEntity.ok(response);
    }
}