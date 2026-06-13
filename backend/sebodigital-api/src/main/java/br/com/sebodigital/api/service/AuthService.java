package br.com.sebodigital.api.service;

import br.com.sebodigital.api.dto.auth.AuthResponse;
import br.com.sebodigital.api.dto.auth.CadastroUsuarioRequest;
import br.com.sebodigital.api.dto.auth.LoginRequest;
import br.com.sebodigital.api.dto.auth.UsuarioResponse;
import br.com.sebodigital.api.model.entity.Usuario;
import br.com.sebodigital.api.model.enums.UsuarioRole;
import br.com.sebodigital.api.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
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
    public AuthResponse loginOAuth(String email, String nome) {
        String emailNormalizado = normalizarEmail(email);
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(emailNormalizado)
                .orElseGet(() -> criarUsuarioOAuth(emailNormalizado, nome));

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

    private Usuario criarUsuarioOAuth(String email, String nome) {
        Usuario usuario = new Usuario();
        usuario.setNome(normalizarNome(nome, email));
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(UUID.randomUUID().toString()));
        usuario.setRole(UsuarioRole.USER);
        return usuarioRepository.save(usuario);
    }

    private String normalizarNome(String nome, String email) {
        if (nome != null && !nome.isBlank()) {
            return nome.trim();
        }

        int arroba = email.indexOf('@');
        return arroba > 0 ? email.substring(0, arroba) : "Usuario Sebo Digital";
    }
}
