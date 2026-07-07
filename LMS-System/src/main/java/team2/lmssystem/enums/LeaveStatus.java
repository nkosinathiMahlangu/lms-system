package team2.lmssystem.enums;

/**
 * Lifecycle states of a leave request.
 * Flow: PENDING → APPROVED (balance deducted) or REJECTED (balance unchanged).
 */
public enum LeaveStatus {
    PENDING,
    APPROVED,
    REJECTED
}
