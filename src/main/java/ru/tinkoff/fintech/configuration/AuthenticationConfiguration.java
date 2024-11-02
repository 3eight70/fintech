package ru.tinkoff.fintech.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ru.tinkoff.fintech.configuration.properties.AuthenticationProperties;

@Configuration
@EnableConfigurationProperties(AuthenticationProperties.class)
public class AuthenticationConfiguration {
}
