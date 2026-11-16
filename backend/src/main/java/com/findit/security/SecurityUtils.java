package com.findit.security;

import com.findit.enums.Role;
import com.findit.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static Optional<UserPrincipal> getCurrentUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return Optional.of(userPrincipal);
        }
        return Optional.empty();
    }

    public static UserPrincipal getRequiredCurrentUserPrincipal() {
        return getCurrentUserPrincipal()
                .orElseThrow(() -> new UnauthorizedException("User is not authenticated"));
    }

    public static Long getCurrentUserId() {
        return getRequiredCurrentUserPrincipal().getId();
    }

    public static String getCurrentUserEmail() {
        return getRequiredCurrentUserPrincipal().getEmail();
    }

    public static boolean hasRole(Role role) {
        return getCurrentUserPrincipal()
                .map(p -> p.getRole() == role)
                .orElse(false);
    }

    public static boolean isAdminOrSuperAdmin() {
        return getCurrentUserPrincipal()
                .map(p -> p.getRole() == Role.ROLE_ADMIN || p.getRole() == Role.ROLE_SUPER_ADMIN)
                .orElse(false);
    }
}
