package br.com.sebodigital.api.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final OAuth2RedirectResolver redirectResolver;

    public OAuth2AuthenticationFailureHandler(OAuth2RedirectResolver redirectResolver) {
        this.redirectResolver = redirectResolver;
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        String redirectUrl = redirectResolver.withFragment(
                redirectResolver.resolve(request),
                Map.of(
                        "oauth", "erro",
                        "mensagem", "Nao foi possivel concluir o login social"));

        response.addHeader(HttpHeaders.SET_COOKIE, redirectResolver.expireCookie().toString());
        response.sendRedirect(redirectUrl);
    }
}
