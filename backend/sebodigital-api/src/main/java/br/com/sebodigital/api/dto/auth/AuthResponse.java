package br.com.sebodigital.api.dto.auth;

import java.time.Instant;

public record AuthResponse(
        String token,
        String tipo,
        Instant expiraEm,
        UsuarioResponse usuario) {
}
