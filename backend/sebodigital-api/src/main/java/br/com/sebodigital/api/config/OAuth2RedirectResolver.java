package br.com.sebodigital.api.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

@Component
public class OAuth2RedirectResolver {

    public static final String COOKIE_NAME = "sebo_oauth_redirect";

    private final String fallbackLoginUrl;
    private final List<String> allowedOrigins;
    private final boolean secureCookie;

    public OAuth2RedirectResolver(
            @Value("${app.frontend.login-url:http://localhost:5500/login.html}") String fallbackLoginUrl,
            @Value("${app.frontend.allowed-redirect-origins:http://localhost:5500,http://127.0.0.1:5500,http://localhost:3000,http://127.0.0.1:3000}") String allowedOrigins) {
        this.fallbackLoginUrl = fallbackLoginUrl;
        this.secureCookie = "https".equalsIgnoreCase(URI.create(fallbackLoginUrl).getScheme());
        this.allowedOrigins = new ArrayList<>(Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .toList());

        String fallbackOrigin = originOf(URI.create(fallbackLoginUrl));
        if (StringUtils.hasText(fallbackOrigin) && !this.allowedOrigins.contains(fallbackOrigin)) {
            this.allowedOrigins.add(fallbackOrigin);
        }
    }

    public String resolve(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return fallbackLoginUrl;
        }

        for (Cookie cookie : request.getCookies()) {
            if (COOKIE_NAME.equals(cookie.getName())) {
                return sanitize(decode(cookie.getValue()));
            }
        }

        return fallbackLoginUrl;
    }

    public ResponseCookie createCookie(String redirectUri) {
        String sanitizedRedirectUri = sanitize(redirectUri);
        boolean secure = "https".equalsIgnoreCase(URI.create(sanitizedRedirectUri).getScheme());

        return ResponseCookie.from(COOKIE_NAME, encode(sanitizedRedirectUri))
                .path("/")
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax")
                .maxAge(Duration.ofMinutes(10))
                .build();
    }

    public ResponseCookie expireCookie() {
        return ResponseCookie.from(COOKIE_NAME, "")
                .path("/")
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Lax")
                .maxAge(Duration.ZERO)
                .build();
    }

    public String sanitize(String redirectUri) {
        if (!StringUtils.hasText(redirectUri)) {
            return fallbackLoginUrl;
        }

        try {
            URI uri = URI.create(redirectUri);
            String origin = originOf(uri);
            if (allowedOrigins.contains(origin)) {
                return redirectUri;
            }
        } catch (IllegalArgumentException ignored) {
            return fallbackLoginUrl;
        }

        return fallbackLoginUrl;
    }

    public String withFragment(String redirectUri, Map<String, ?> parameters) {
        String baseUrl = UriComponentsBuilder.fromUriString(sanitize(redirectUri))
                .fragment(null)
                .build()
                .toUriString();
        String fragment = parameters.entrySet().stream()
                .map(entry -> encodeFragmentValue(entry.getKey())
                        + "="
                        + encodeFragmentValue(entry.getValue()))
                .collect(Collectors.joining("&"));
        return baseUrl + "#" + fragment;
    }

    private String originOf(URI uri) {
        if (!StringUtils.hasText(uri.getScheme()) || !StringUtils.hasText(uri.getHost())) {
            return "";
        }

        String port = uri.getPort() >= 0 ? ":" + uri.getPort() : "";
        return uri.getScheme() + "://" + uri.getHost() + port;
    }

    private String encode(String value) {
        return Base64.getUrlEncoder()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String decode(String value) {
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(value);
            return new String(decoded, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ignored) {
            return fallbackLoginUrl;
        }
    }

    private String encodeFragmentValue(Object value) {
        return UriUtils.encodeQueryParam(value == null ? "" : value.toString(), StandardCharsets.UTF_8);
    }
}
