package ru.tinkoff.fintech.configuration.properties;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("authentication")
public class AuthenticationProperties {
    private final String secret;
    private final Duration expiration;
    private final Duration rememberMeExpiration;

    public AuthenticationProperties(
            String secret,
            Duration expiration,
            Duration rememberMeExpiration
    ) {
        this.secret = secret;
        this.expiration = expiration;
        this.rememberMeExpiration = rememberMeExpiration;
    }

    public String getSecret() {
        return secret;
    }

    public Duration getExpiration() {
        return expiration;
    }

    public Duration getRememberMeExpiration() {
        return rememberMeExpiration;
    }
}
