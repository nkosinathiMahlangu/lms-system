package team2.lmssystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.lmssystem.dto.request.ApplyLeaveRequest;
import team2.lmssystem.dto.respond.ApiResponse;
import team2.lmssystem.dto.respond.LeaveResponse;
import team2.lmssystem.service.leaveservice.LeaveService;

import java.security.Principal;
import java.util.List;

/** Employee-only endpoints — requires ROLE_EMPLOYEE. */
@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final LeaveService leaveService;

    @PostMapping("/leave/apply")
    public ResponseEntity<ApiResponse<String>> applyLeave(
            @Valid @RequestBody ApplyLeaveRequest request,
            Principal principal) {

        // Username sourced from JWT — employees can only act on their own account
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message(leaveService.applyLeave(principal.getName(), request))
                .build());
    }

    @GetMapping("/leave")
    public ResponseEntity<ApiResponse<List<LeaveResponse>>> getMyLeaves(Principal principal) {

        return ResponseEntity.ok(ApiResponse.<List<LeaveResponse>>builder()
                .success(true)
                .message("Leave requests fetched successfully")
                .data(leaveService.getUserLeaves(principal.getName()))
                .build());
    }
}
