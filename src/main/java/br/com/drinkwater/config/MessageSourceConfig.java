package br.com.drinkwater.config;

import br.com.drinkwater.config.properties.MessageSourceProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class MessageSourceConfig {

    private final MessageSourceProperties messageSourceProperties;

    public MessageSourceConfig(MessageSourceProperties messageSourceProperties) {
        this.messageSourceProperties = messageSourceProperties;
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages");
        messageSource.setDefaultEncoding(messageSourceProperties.encoding());
        messageSource.setFallbackToSystemLocale(false);

        return messageSource;
    }
}