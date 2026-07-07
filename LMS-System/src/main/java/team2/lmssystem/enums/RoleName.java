package team2.lmssystem.enums;

/**
 * User roles in the LMS.
 * Used with hasRole() in SecurityConfig — Spring prepends "ROLE_" automatically,
 * so the stored authority must be plain "ADMIN" / "EMPLOYEE", not "ROLE_ADMIN".
 */
public enum RoleName {
    ADMIN,
    EMPLOYEE
}
