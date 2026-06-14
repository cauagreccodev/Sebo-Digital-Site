package br.com.sebodigital.api.config;

import br.com.sebodigital.api.dto.auth.AuthResponse;
import br.com.sebodigital.api.dto.auth.UsuarioResponse;
import br.com.sebodigital.api.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final OAuth2RedirectResolver redirectResolver;

    public OAuth2AuthenticationSuccessHandler(
            AuthService authService,
            OAuth2RedirectResolver redirectResolver) {
        this.authService = authService;
        this.redirectResolver = redirectResolver;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        try {
            if (!(authentication instanceof OAuth2AuthenticationToken oauthToken)) {
                throw new IllegalArgumentException("Login social invalido");
            }

            OAuth2User principal = oauthToken.getPrincipal();
            String registrationId = oauthToken.getAuthorizedClientRegistrationId();
            String providerId = resolveProviderId(registrationId, principal);
            String email = attribute(principal, "email");
            if (!StringUtils.hasText(email)) {
                throw new IllegalArgumentException("O provedor nao retornou um e-mail valido");
            }

            String nome = resolveName(principal, email);
            String fotoUrl = resolvePicture(principal);
            AuthResponse auth = authService.loginOAuth(registrationId, providerId, email, nome, fotoUrl);
            UsuarioResponse usuario = auth.usuario();
            String redirectUrl = UriComponentsBuilder.fromUriString(redirectResolver.resolve(request))
                    .queryParam("oauth", "success")
                    .queryParam("token", auth.token())
                    .queryParam("tipo", auth.tipo())
                    .queryParam("expiraEm", auth.expiraEm())
                    .queryParam("usuarioId", usuario.id())
                    .queryParam("nome", usuario.nome())
                    .queryParam("email", usuario.email())
                    .queryParam("role", usuario.role())
                    .queryParam("authProvider", usuario.authProvider())
                    .queryParam("fotoUrl", usuario.fotoUrl())
                    .build()
                    .encode()
                    .toUriString();

            response.addHeader(HttpHeaders.SET_COOKIE, redirectResolver.expireCookie().toString());
            response.sendRedirect(redirectUrl);
        } catch (RuntimeException ex) {
            redirectWithError(request, response, ex.getMessage());
        }
    }

    private void redirectWithError(
            HttpServletRequest request,
            HttpServletResponse response,
            String message) throws IOException {
        String redirectUrl = UriComponentsBuilder.fromUriString(redirectResolver.resolve(request))
                .queryParam("oauth", "erro")
                .queryParam("mensagem", message)
                .build()
                .encode()
                .toUriString();

        response.addHeader(HttpHeaders.SET_COOKIE, redirectResolver.expireCookie().toString());
        response.sendRedirect(redirectUrl);
    }

    private String resolveName(OAuth2User principal, String email) {
        String nome = attribute(principal, "name");
        if (StringUtils.hasText(nome)) {
            return nome;
        }

        String givenName = attribute(principal, "given_name");
        if (StringUtils.hasText(givenName)) {
            return givenName;
        }

        int arroba = email.indexOf('@');
        return arroba > 0 ? email.substring(0, arroba) : "Usuario Sebo Digital";
    }

    private String resolveProviderId(String registrationId, OAuth2User principal) {
        String providerIdAttribute = switch (registrationId) {
            case "google" -> "sub";
            case "facebook" -> "id";
            default -> "";
        };

        String providerId = attribute(principal, providerIdAttribute);
        return StringUtils.hasText(providerId) ? providerId : principal.getName();
    }

    private String resolvePicture(OAuth2User principal) {
        Object picture = principal.getAttributes().get("picture");
        if (picture instanceof String value && StringUtils.hasText(value)) {
            return value.trim();
        }

        if (picture instanceof Map<?, ?> pictureMap) {
            String directUrl = stringValue(pictureMap.get("url"));
            if (StringUtils.hasText(directUrl)) {
                return directUrl;
            }

            Object data = pictureMap.get("data");
            if (data instanceof Map<?, ?> dataMap) {
                String nestedUrl = stringValue(dataMap.get("url"));
                if (StringUtils.hasText(nestedUrl)) {
                    return nestedUrl;
                }
            }
        }

        return "";
    }

    private String attribute(OAuth2User principal, String name) {
        if (!StringUtils.hasText(name)) {
            return "";
        }

        Object value = principal.getAttributes().get(name);
        return stringValue(value);
    }

    private String stringValue(Object value) {
        return value == null ? "" : value.toString().trim();
    }
}
