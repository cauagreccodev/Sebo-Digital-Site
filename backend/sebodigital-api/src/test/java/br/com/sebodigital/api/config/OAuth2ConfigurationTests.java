package br.com.sebodigital.api.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

class OAuth2ConfigurationTests {

    @Test
    void registersProvidersOnlyWithValidCredentials() {
        OAuth2ClientConfig config = new OAuth2ClientConfig();

        ClientRegistrationRepository repository = config.clientRegistrationRepository(
                "client.apps.googleusercontent.com",
                "google-secret",
                "123456789",
                "facebook-secret");

        assertNotNull(repository.findByRegistrationId("google"));
        assertNotNull(repository.findByRegistrationId("facebook"));
        assertNull(repository.findByRegistrationId("unsupported"));
    }

    @Test
    void ignoresInvalidProviderCredentials() {
        OAuth2ClientConfig config = new OAuth2ClientConfig();

        ClientRegistrationRepository repository = config.clientRegistrationRepository(
                "invalid-google-client",
                "google-secret",
                "facebook-app-name",
                "facebook-secret");

        assertNull(repository.findByRegistrationId("google"));
        assertNull(repository.findByRegistrationId("facebook"));
    }

    @Test
    void usesSecureCookieAndFragmentForProductionRedirect() {
        OAuth2RedirectResolver resolver = new OAuth2RedirectResolver(
                "https://sebo-digital-site.vercel.app/login.html",
                "https://sebo-digital-site.vercel.app");

        ResponseCookie cookie = resolver.createCookie(
                "https://sebo-digital-site.vercel.app/login.html");
        String redirectUrl = resolver.withFragment(
                "https://sebo-digital-site.vercel.app/login.html",
                Map.of(
                        "oauth", "success",
                        "token", "header.payload.signature",
                        "nome", "Usuario Teste"));

        assertTrue(cookie.isSecure());
        assertTrue(cookie.isHttpOnly());
        assertEquals("Lax", cookie.getSameSite());
        assertFalse(redirectUrl.contains("?token="));
        assertTrue(redirectUrl.startsWith(
                "https://sebo-digital-site.vercel.app/login.html#"));
        assertTrue(redirectUrl.contains("token=header.payload.signature"));
        assertTrue(redirectUrl.contains("nome=Usuario%20Teste"));
    }

    @Test
    void rejectsRedirectOutsideConfiguredOrigins() {
        OAuth2RedirectResolver resolver = new OAuth2RedirectResolver(
                "https://sebo-digital-site.vercel.app/login.html",
                "https://sebo-digital-site.vercel.app");

        assertEquals(
                "https://sebo-digital-site.vercel.app/login.html",
                resolver.sanitize("https://example.com/steal-token"));
    }
}
