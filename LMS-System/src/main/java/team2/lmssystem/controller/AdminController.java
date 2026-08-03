package team2.lmssystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.lmssystem.dto.request.CreateUserRequest;
import team2.lmssystem.dto.request.LeaveActionRequest;
import team2.lmssystem.dto.request.UpdateLeaveTypeRequest;
import team2.lmssystem.dto.respond.ApiResponse;
import team2.lmssystem.dto.respond.LeaveResponse;
import team2.lmssystem.dto.respond.LeaveTypeResponse;
import team2.lmssystem.dto.respond.UserResponse;
import team2.lmssystem.service.leaveservice.LeaveService;
import team2.lmssystem.service.userservice.UserService;

import java.security.Principal;
import java.util.List;

/** Admin-only endpoints — requires ROLE_ADMIN. */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final LeaveService leaveService;

    // -------------------------------------------------------------------------
    // User management
    // -------------------------------------------------------------------------

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<String>> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message(userService.createUser(request))
                .build());
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {

        return ResponseEntity.ok(ApiResponse.<List<UserResponse>>builder()
                .success(true)
                .message("Users fetched successfully")
                .data(userService.getAllUsers())
                .build());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message(userService.deleteUser(id))
                .build());
    }

    // -------------------------------------------------------------------------
    // Leave request management
    // -------------------------------------------------------------------------

    /** Get all leave requests. Optional ?status=PENDING/APPROVED/REJECTED filter. */
    @GetMapping("/leave")
    public ResponseEntity<ApiResponse<List<LeaveResponse>>> getAllLeaves(
            @RequestParam(required = false) String status) {

        return ResponseEntity.ok(ApiResponse.<List<LeaveResponse>>builder()
                .success(true)
                .message("Leave requests fetched successfully")
                .data(leaveService.getAllLeaveRequests(status))
                .build());
    }

    @PutMapping("/leave/action")
    public ResponseEntity<ApiResponse<String>> processLeave(
            @Valid @RequestBody LeaveActionRequest request,
            Principal principal) {

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message(leaveService.approveOrRejectLeave(principal.getName(), request))
                .build());
    }

    // -------------------------------------------------------------------------
    // Leave type management
    // -------------------------------------------------------------------------

    /** Get all leave types — used to populate dropdowns on the frontend. */
    @GetMapping("/leave-types")
    public ResponseEntity<ApiResponse<List<LeaveTypeResponse>>> getAllLeaveTypes() {

        return ResponseEntity.ok(ApiResponse.<List<LeaveTypeResponse>>builder()
                .success(true)
                .message("Leave types fetched successfully")
                .data(leaveService.getAllLeaveTypes())
                .build());
    }

    @PutMapping("/leave-types/{id}")
    public ResponseEntity<ApiResponse<LeaveTypeResponse>> updateLeaveType(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLeaveTypeRequest request) {

        return ResponseEntity.ok(ApiResponse.<LeaveTypeResponse>builder()
                .success(true)
                .message("Leave type updated successfully")
                .data(leaveService.updateLeaveType(id, request))
                .build());
    }

    @DeleteMapping("/leave-types/{id}")
    public ResponseEntity<ApiResponse<String>> deleteLeaveType(@PathVariable Long id) {

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message(leaveService.deleteLeaveType(id))
                .build());
    }
}
