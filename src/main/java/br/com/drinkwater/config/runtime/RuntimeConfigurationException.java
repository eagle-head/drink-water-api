package br.com.drinkwater.config.runtime;

/**
 * Domain exception thrown when a runtime configuration change fails to be applied (e.g. invalid log
 * level, system-level error during reconfiguration).
 */
public class RuntimeConfigurationException extends RuntimeException {

    public RuntimeConfigurationException(String message) {
        super(message);
    }

    public RuntimeConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
