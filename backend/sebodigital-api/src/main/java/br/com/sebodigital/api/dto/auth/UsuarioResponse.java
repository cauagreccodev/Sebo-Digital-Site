package br.com.sebodigital.api.dto.auth;

import br.com.sebodigital.api.model.entity.Usuario;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String role) {

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole().name());
    }
}
