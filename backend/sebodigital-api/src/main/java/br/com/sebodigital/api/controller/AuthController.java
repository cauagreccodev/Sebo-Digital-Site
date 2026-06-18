package br.com.sebodigital.api.controller;

import br.com.sebodigital.api.config.OAuth2RedirectResolver;
import br.com.sebodigital.api.dto.auth.AuthResponse;
import br.com.sebodigital.api.dto.auth.CadastroUsuarioRequest;
import br.com.sebodigital.api.dto.auth.LoginRequest;
import br.com.sebodigital.api.dto.auth.UsuarioResponse;
import br.com.sebodigital.api.service.AuthService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Set<String> OAUTH_PROVIDERS = Set.of("google", "facebook");

    private final AuthService authService;
    private final OAuth2RedirectResolver redirectResolver;
    private final ObjectProvider<ClientRegistrationRepository> clientRegistrationRepository;

    public AuthController(
            AuthService authService,
            OAuth2RedirectResolver redirectResolver,
            ObjectProvider<ClientRegistrationRepository> clientRegistrationRepository) {
        this.authService = authService;
        this.redirectResolver = redirectResolver;
        this.clientRegistrationRepository = clientRegistrationRepository;
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

        String erroConfiguracao = erroConfiguracaoOAuth(providerNormalizado);
        if (erroConfiguracao != null) {
            String redirectUrl = UriComponentsBuilder.fromUriString(redirectResolver.sanitize(redirectUri))
                    .queryParam("oauth", "erro")
                    .queryParam("mensagem", erroConfiguracao)
                    .build()
                    .encode()
                    .toUriString();

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(redirectUrl))
                    .build();
        }

        ResponseCookie redirectCookie = redirectResolver.createCookie(redirectUri);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/oauth2/authorization/" + providerNormalizado))
                .header(HttpHeaders.SET_COOKIE, redirectCookie.toString())
                .build();
    }

    @GetMapping("/oauth2/providers")
    public Map<String, Boolean> provedoresOAuth() {
        return Map.of(
                "google", provedorConfigurado("google"),
                "facebook", provedorConfigurado("facebook"));
    }

    @GetMapping("/me")
    public UsuarioResponse me(Authentication authentication) {
        return authService.buscarPorEmail(authentication.getName());
    }

    private String erroConfiguracaoOAuth(String provider) {
        ClientRegistrationRepository repository = clientRegistrationRepository.getIfAvailable();
        if (repository == null) {
            return "A API nao carregou a configuracao OAuth2. Reinicie a API e verifique as credenciais.";
        }

        ClientRegistration registration = repository.findByRegistrationId(provider);
        if (registration == null) {
            if ("facebook".equals(provider)) {
                return "Configure FACEBOOK_CLIENT_ID numerico e FACEBOOK_CLIENT_SECRET e reinicie a API.";
            }

            return "Configure GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET e reinicie a API.";
        }

        String clientId = registration.getClientId();
        if (clientId == null || clientId.isBlank() || clientId.contains("${") || clientId.contains("}")) {
            return "O Client ID OAuth2 de " + provider + " esta vazio ou com placeholder.";
        }

        if ("facebook".equals(provider) && !clientId.chars().allMatch(Character::isDigit)) {
            return "O FACEBOOK_CLIENT_ID precisa ser o App ID numerico da Meta.";
        }

        return null;
    }

    private boolean provedorConfigurado(String provider) {
        ClientRegistrationRepository repository = clientRegistrationRepository.getIfAvailable();
        return repository != null && repository.findByRegistrationId(provider) != null;
    }
}
