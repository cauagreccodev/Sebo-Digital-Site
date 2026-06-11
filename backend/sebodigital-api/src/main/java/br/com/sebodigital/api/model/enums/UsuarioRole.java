package br.com.sebodigital.api.model.enums;

public enum UsuarioRole {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String authority;

    UsuarioRole(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }
}
