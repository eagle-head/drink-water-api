package br.com.drinkwater.config;

import br.com.drinkwater.config.properties.LocaleProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@Configuration
public class LocaleConfig {

    private final LocaleProperties localeProperties;

    public LocaleConfig(LocaleProperties localeProperties) {
        this.localeProperties = localeProperties;
    }

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(localeProperties.getDefaultLocale());
        resolver.setSupportedLocales(localeProperties.getSupportedLocales());

        return resolver;
    }
}