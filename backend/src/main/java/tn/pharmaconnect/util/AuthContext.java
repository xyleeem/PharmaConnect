package tn.pharmaconnect.util;

import tn.pharmaconnect.entity.Role;
import tn.pharmaconnect.exception.ApiException;
import org.springframework.http.HttpStatus;

public final class AuthContext {
    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USER_ROLE = "X-User-Role";

    private AuthContext() {
    }

    public static Integer requireUserId(String headerValue) {
        if (headerValue == null || headerValue.isBlank()) {
            throw new ApiException("Authentification requise (X-User-Id manquant)", HttpStatus.UNAUTHORIZED);
        }
        try {
            return Integer.parseInt(headerValue.trim());
        } catch (NumberFormatException e) {
            throw new ApiException("X-User-Id invalide", HttpStatus.BAD_REQUEST);
        }
    }

    public static Role requireRole(String headerValue) {
        if (headerValue == null || headerValue.isBlank()) {
            throw new ApiException("Authentification requise (X-User-Role manquant)", HttpStatus.UNAUTHORIZED);
        }
        try {
            return Role.valueOf(headerValue.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApiException("X-User-Role invalide", HttpStatus.BAD_REQUEST);
        }
    }

    public static void requirePatient(Role role) {
        if (role != Role.PATIENT) {
            throw new ApiException("Acces reserve aux patients", HttpStatus.FORBIDDEN);
        }
    }

    public static void requirePharmacyStaff(Role role) {
        if (role != Role.PHARMACIEN && role != Role.ADMIN) {
            throw new ApiException("Acces reserve au pharmacien ou administrateur", HttpStatus.FORBIDDEN);
        }
    }

    public static void requireAdmin(Role role) {
        if (role != Role.ADMIN) {
            throw new ApiException("Acces reserve a l'administrateur", HttpStatus.FORBIDDEN);
        }
    }

    public static void requireOwnPatient(Integer loggedUserId, Integer patientId) {
        if (!loggedUserId.equals(patientId)) {
            throw new ApiException("Acces refuse: vous ne pouvez consulter que vos propres donnees", HttpStatus.FORBIDDEN);
        }
    }
}
