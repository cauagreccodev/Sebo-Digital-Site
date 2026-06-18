package br.com.sebodigital.api.dto.auth;

import br.com.sebodigital.api.model.entity.Usuario;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String role,
        String authProvider,
        String fotoUrl,
        String telefone,
        String enderecoPrincipal,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        String cep) {

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole().name(),
                usuario.getAuthProvider() == null ? "LOCAL" : usuario.getAuthProvider().name(),
                usuario.getFotoUrl(),
                usuario.getTelefone(),
                usuario.getEnderecoPrincipal(),
                usuario.getComplemento(),
                usuario.getBairro(),
                usuario.getCidade(),
                usuario.getEstado(),
                usuario.getCep());
    }
}
