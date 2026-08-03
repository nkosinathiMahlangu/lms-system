package team2.lmssystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.lmssystem.dto.request.ApplyLeaveRequest;
import team2.lmssystem.dto.respond.ApiResponse;
import team2.lmssystem.dto.respond.LeaveBalanceResponse;
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

    /** Submit a new leave request. */
    @PostMapping("/leave/apply")
    public ResponseEntity<ApiResponse<String>> applyLeave(
            @Valid @RequestBody ApplyLeaveRequest request,
            Principal principal) {

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message(leaveService.applyLeave(principal.getName(), request))
                .build());
    }

    /** View all own leave requests. */
    @GetMapping("/leave")
    public ResponseEntity<ApiResponse<List<LeaveResponse>>> getMyLeaves(Principal principal) {

        return ResponseEntity.ok(ApiResponse.<List<LeaveResponse>>builder()
                .success(true)
                .message("Leave requests fetched successfully")
                .data(leaveService.getUserLeaves(principal.getName()))
                .build());
    }

    /** Cancel a PENDING leave request. */
    @DeleteMapping("/leave/{id}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelLeave(
            @PathVariable Long id,
            Principal principal) {

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message(leaveService.cancelLeave(principal.getName(), id))
                .build());
    }

    /** View remaining leave days per leave type. */
    @GetMapping("/leave/balances")
    public ResponseEntity<ApiResponse<List<LeaveBalanceResponse>>> getBalances(Principal principal) {

        return ResponseEntity.ok(ApiResponse.<List<LeaveBalanceResponse>>builder()
                .success(true)
                .message("Leave balances fetched successfully")
                .data(leaveService.getLeaveBalances(principal.getName()))
                .build());
    }
}
