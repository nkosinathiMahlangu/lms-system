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

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final LeaveService leaveService;

    // Apply Leave
    @PostMapping("/leave/apply")
    public ResponseEntity<ApiResponse<String>> applyLeave(
            @Valid @RequestBody ApplyLeaveRequest request,
            Principal principal) {

        String response = leaveService.applyLeave(
                principal.getName(),
                request
        );

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message(response)
                        .build()
        );
    }

    // 📄 View Own Leaves
    @GetMapping("/leave")
    public ResponseEntity<ApiResponse<List<LeaveResponse>>> getMyLeaves(
            Principal principal) {

        List<LeaveResponse> leaves = leaveService.getUserLeaves(
                principal.getName()
        );

        return ResponseEntity.ok(
                ApiResponse.<List<LeaveResponse>>builder()
                        .success(true)
                        .message("Leave requests fetched")
                        .data(leaves)
                        .build()
        );
    }
}
