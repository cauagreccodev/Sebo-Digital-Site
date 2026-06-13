package br.com.sebodigital.api.controller;

import br.com.sebodigital.api.config.OAuth2RedirectResolver;
import br.com.sebodigital.api.dto.auth.AuthResponse;
import br.com.sebodigital.api.dto.auth.CadastroUsuarioRequest;
import br.com.sebodigital.api.dto.auth.LoginRequest;
import br.com.sebodigital.api.dto.auth.UsuarioResponse;
import br.com.sebodigital.api.service.AuthService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Set<String> OAUTH_PROVIDERS = Set.of("google", "facebook");

    private final AuthService authService;
    private final OAuth2RedirectResolver redirectResolver;

    public AuthController(AuthService authService, OAuth2RedirectResolver redirectResolver) {
        this.authService = authService;
        this.redirectResolver = redirectResolver;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<AuthResponse> cadastrar(@Valid @RequestBody CadastroUsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.cadastrar(request));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/oauth2/{provider}")
    public ResponseEntity<Void> iniciarLoginSocial(
            @PathVariable String provider,
            @RequestParam(name = "redirect_uri", required = false) String redirectUri) {
        String providerNormalizado = provider.toLowerCase();
        if (!OAUTH_PROVIDERS.contains(providerNormalizado)) {
            throw new IllegalArgumentException("Provedor de login social nao suportado");
        }

        ResponseCookie redirectCookie = redirectResolver.createCookie(redirectUri);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/oauth2/authorization/" + providerNormalizado))
                .header(HttpHeaders.SET_COOKIE, redirectCookie.toString())
                .build();
    }

    @GetMapping("/me")
    public UsuarioResponse me(Authentication authentication) {
        return authService.buscarPorEmail(authentication.getName());
    }
}
