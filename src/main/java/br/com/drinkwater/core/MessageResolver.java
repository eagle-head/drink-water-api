package br.com.drinkwater.core;

import java.util.Objects;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * Convenience wrapper around {@link MessageSource} that resolves i18n messages using the current
 * locale from {@link LocaleContextHolder}.
 */
@Component
public class MessageResolver {

    private final MessageSource messageSource;

    public MessageResolver(MessageSource messageSource) {
        this.messageSource = Objects.requireNonNull(messageSource);
    }

    /**
     * Resolves a message by key using the current locale.
     *
     * @param key the message key
     * @return the resolved message
     */
    public String resolve(String key) {
        return Objects.requireNonNull(
                messageSource.getMessage(key, null, LocaleContextHolder.getLocale()));
    }

    /**
     * Resolves a message by key with arguments using the current locale.
     *
     * @param key the message key
     * @param args the message arguments for placeholder substitution
     * @return the resolved message
     */
    public String resolve(String key, Object... args) {
        return Objects.requireNonNull(
                messageSource.getMessage(key, args, LocaleContextHolder.getLocale()));
    }
}
