package br.com.drinkwater.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "webhook")
@Validated
public record WebhookProperties(
        @NotBlank(message = "WEBHOOK_SECRET environment variable is required") String secret) {}
