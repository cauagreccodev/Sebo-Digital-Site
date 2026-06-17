package br.com.sebodigital.api.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.util.StringUtils;

@Configuration
public class OAuth2ClientConfig {

    @Bean
    ClientRegistrationRepository clientRegistrationRepository(
            @Value("${app.oauth2.google.client-id:}") String googleClientId,
            @Value("${app.oauth2.google.client-secret:}") String googleClientSecret,
            @Value("${app.oauth2.facebook.client-id:}") String facebookClientId,
            @Value("${app.oauth2.facebook.client-secret:}") String facebookClientSecret) {
        List<ClientRegistration> registrations = new ArrayList<>();

        if (hasValidGoogleCredentials(googleClientId, googleClientSecret)) {
            registrations.add(CommonOAuth2Provider.GOOGLE.getBuilder("google")
                    .clientId(googleClientId)
                    .clientSecret(googleClientSecret)
                    .scope("openid", "profile", "email")
                    .build());
        }

        if (hasValidFacebookCredentials(facebookClientId, facebookClientSecret)) {
            registrations.add(CommonOAuth2Provider.FACEBOOK.getBuilder("facebook")
                    .clientId(facebookClientId)
                    .clientSecret(facebookClientSecret)
                    .scope("email", "public_profile")
                    .build());
        }

        return new OptionalClientRegistrationRepository(registrations);
    }

    private boolean hasValidGoogleCredentials(String clientId, String clientSecret) {
        return hasValue(clientId)
                && hasValue(clientSecret)
                && clientId.endsWith(".apps.googleusercontent.com");
    }

    private boolean hasValidFacebookCredentials(String clientId, String clientSecret) {
        return hasValue(clientId)
                && hasValue(clientSecret)
                && clientId.chars().allMatch(Character::isDigit);
    }

    private boolean hasValue(String value) {
        return StringUtils.hasText(value) && !value.contains("${") && !value.contains("}");
    }

    private static final class OptionalClientRegistrationRepository
            implements ClientRegistrationRepository, Iterable<ClientRegistration> {

        private final Map<String, ClientRegistration> registrations;

        private OptionalClientRegistrationRepository(List<ClientRegistration> registrations) {
            this.registrations = registrations.stream()
                    .collect(Collectors.toUnmodifiableMap(
                            ClientRegistration::getRegistrationId,
                            Function.identity()));
        }

        @Override
        public ClientRegistration findByRegistrationId(String registrationId) {
            return registrations.get(registrationId);
        }

        @Override
        public Iterator<ClientRegistration> iterator() {
            return registrations.values().iterator();
        }
    }
}
