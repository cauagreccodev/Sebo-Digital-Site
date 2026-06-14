package br.com.sebodigital.api.service;

import br.com.sebodigital.api.dto.auth.AuthResponse;
import br.com.sebodigital.api.dto.auth.CadastroUsuarioRequest;
import br.com.sebodigital.api.dto.auth.LoginRequest;
import br.com.sebodigital.api.dto.auth.UsuarioResponse;
import br.com.sebodigital.api.model.entity.Usuario;
import br.com.sebodigital.api.model.enums.AuthProvider;
import br.com.sebodigital.api.model.enums.UsuarioRole;
import br.com.sebodigital.api.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse cadastrar(CadastroUsuarioRequest request) {
        String email = normalizarEmail(request.email());
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Ja existe um usuario cadastrado com este e-mail");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome().trim());
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuario.setRole(UsuarioRole.USER);
        usuario.setAuthProvider(AuthProvider.LOCAL);

        Usuario salvo = usuarioRepository.save(usuario);
        return jwtService.gerarToken(salvo);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = normalizarEmail(request.email());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.senha()));

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado"));
        return jwtService.gerarToken(usuario);
    }

    @Transactional
    public AuthResponse loginOAuth(
            String registrationId,
            String providerId,
            String email,
            String nome,
            String fotoUrl) {
        AuthProvider authProvider = AuthProvider.fromRegistrationId(registrationId);
        String emailNormalizado = normalizarEmail(email);
        String providerIdNormalizado = normalizarProviderId(providerId);

        Usuario usuario = buscarUsuarioOAuth(authProvider, providerIdNormalizado, emailNormalizado)
                .orElseGet(() -> criarUsuarioOAuth(authProvider, providerIdNormalizado, emailNormalizado, nome, fotoUrl));
        atualizarDadosOAuth(usuario, authProvider, providerIdNormalizado, nome, fotoUrl);

        return jwtService.gerarToken(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado"));
        return UsuarioResponse.from(usuario);
    }

    private String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private Optional<Usuario> buscarUsuarioOAuth(AuthProvider authProvider, String providerId, String email) {
        if (!providerId.isBlank()) {
            Optional<Usuario> usuarioPorProvider = usuarioRepository.findByAuthProviderAndProviderId(authProvider, providerId);
            if (usuarioPorProvider.isPresent()) {
                return usuarioPorProvider;
            }
        }

        return usuarioRepository.findByEmailIgnoreCase(email);
    }

    private Usuario criarUsuarioOAuth(
            AuthProvider authProvider,
            String providerId,
            String email,
            String nome,
            String fotoUrl) {
        Usuario usuario = new Usuario();
        usuario.setNome(normalizarNome(nome, email));
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(UUID.randomUUID().toString()));
        usuario.setRole(UsuarioRole.USER);
        usuario.setAuthProvider(authProvider);
        usuario.setProviderId(providerId.isBlank() ? null : providerId);
        usuario.setFotoUrl(normalizarTexto(fotoUrl));
        return usuarioRepository.save(usuario);
    }

    private void atualizarDadosOAuth(
            Usuario usuario,
            AuthProvider authProvider,
            String providerId,
            String nome,
            String fotoUrl) {
        usuario.setAuthProvider(authProvider);
        if (!providerId.isBlank()) {
            usuario.setProviderId(providerId);
        }
        if (nome != null && !nome.isBlank()) {
            usuario.setNome(nome.trim());
        }
        if (fotoUrl != null && !fotoUrl.isBlank()) {
            usuario.setFotoUrl(fotoUrl.trim());
        }
        usuarioRepository.save(usuario);
    }

    private String normalizarNome(String nome, String email) {
        if (nome != null && !nome.isBlank()) {
            return nome.trim();
        }

        int arroba = email.indexOf('@');
        return arroba > 0 ? email.substring(0, arroba) : "Usuario Sebo Digital";
    }

    private String normalizarProviderId(String providerId) {
        return providerId == null ? "" : providerId.trim();
    }

    private String normalizarTexto(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
