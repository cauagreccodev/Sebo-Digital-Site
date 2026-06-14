package br.com.sebodigital.api.service;

import br.com.sebodigital.api.dto.auth.AuthResponse;
import br.com.sebodigital.api.dto.auth.UsuarioResponse;
import br.com.sebodigital.api.model.entity.Usuario;
import java.time.Duration;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final Duration expiration;

    public JwtService(
            JwtEncoder jwtEncoder,
            @Value("${app.security.jwt-expiration-minutes}") long expirationMinutes) {
        this.jwtEncoder = jwtEncoder;
        this.expiration = Duration.ofMinutes(expirationMinutes);
    }

    public AuthResponse gerarToken(Usuario usuario) {
        Instant agora = Instant.now();
        Instant expiraEm = agora.plus(expiration);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("sebodigital-api")
                .issuedAt(agora)
                .expiresAt(expiraEm)
                .subject(usuario.getEmail())
                .claim("usuarioId", usuario.getId())
                .claim("nome", usuario.getNome())
                .claim("role", usuario.getRole().name())
                .claim("authProvider", usuario.getAuthProvider() == null ? "LOCAL" : usuario.getAuthProvider().name())
                .claim("scope", usuario.getRole().getAuthority())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        return new AuthResponse(token, "Bearer", expiraEm, UsuarioResponse.from(usuario));
    }
}
