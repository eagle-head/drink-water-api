package br.com.drinkwater.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

final class MessageResolverTest {

    private MessageSource messageSource;
    private MessageResolver resolver;

    @BeforeEach
    void setUp() {
        messageSource = mock(MessageSource.class);
        resolver = new MessageResolver(messageSource);
        LocaleContextHolder.setLocale(Locale.US);
    }

    @Test
    void givenNullMessageSource_whenConstruct_thenThrowsNullPointerException() {
        assertThatThrownBy(() -> new MessageResolver(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void givenValidKey_whenResolve_thenReturnsMessage() {
        when(messageSource.getMessage(eq("test.key"), isNull(), any(Locale.class)))
                .thenReturn("Test Message");

        var result = resolver.resolve("test.key");

        assertThat(result).isEqualTo("Test Message");
    }

    @Test
    void givenValidKeyWithArgs_whenResolve_thenReturnsFormattedMessage() {
        when(messageSource.getMessage(eq("test.key"), eq(new Object[] {"arg1"}), any(Locale.class)))
                .thenReturn("Test arg1");

        var result = resolver.resolve("test.key", "arg1");

        assertThat(result).isEqualTo("Test arg1");
    }
}
