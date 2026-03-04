package br.com.drinkwater.config;

import br.com.drinkwater.config.security.ScopeAwareAccessDeniedHandler;
import br.com.drinkwater.core.MessageResolver;
import java.util.List;
import java.util.Locale;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@TestConfiguration
@EnableMethodSecurity
public class TestMessageSourceConfig {

    private static final Locale EN_US = Locale.of("en", "US");
    private static final Locale PT_BR = Locale.of("pt", "BR");

    @Bean
    LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(EN_US);
        resolver.setSupportedLocales(List.of(EN_US, PT_BR));
        return resolver;
    }

    @Bean
    MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }

    @Bean
    MessageResolver messageResolver() {
        return new MessageResolver(messageSource());
    }

    @Bean
    LocalValidatorFactoryBean getValidator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }

    @Bean
    ScopeAwareAccessDeniedHandler scopeAwareAccessDeniedHandler() {
        return new ScopeAwareAccessDeniedHandler();
    }
}
