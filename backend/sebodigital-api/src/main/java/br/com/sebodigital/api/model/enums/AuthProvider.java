package br.com.sebodigital.api.model.enums;

public enum AuthProvider {
    LOCAL,
    GOOGLE,
    FACEBOOK;

    public static AuthProvider fromRegistrationId(String registrationId) {
        if (registrationId == null || registrationId.isBlank()) {
            return LOCAL;
        }

        return AuthProvider.valueOf(registrationId.trim().toUpperCase());
    }
}
