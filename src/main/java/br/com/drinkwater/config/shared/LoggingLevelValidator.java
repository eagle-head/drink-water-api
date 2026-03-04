package br.com.drinkwater.config.shared;

/**
 * Pure utility class for evaluating logging level configurations. Centralizes logic shared between
 * build-time {@code LoggingProperties} and runtime {@code RuntimeLoggingConfiguration}.
 */
public final class LoggingLevelValidator {

    private LoggingLevelValidator() {}

    /**
     * Checks if the given set of logging levels is safe for production (WARN or higher).
     *
     * @param root the root logger level
     * @param security the security logger level
     * @param oauth2 the OAuth2 logger level
     * @param sql the SQL logger level
     * @return true if all checked levels are WARN, ERROR, FATAL, or OFF
     */
    public static boolean isProductionSafe(
            String root, String security, String oauth2, String sql) {
        return isLevelSafeForProduction(root)
                && isLevelSafeForProduction(security)
                && isLevelSafeForProduction(oauth2)
                && isLevelSafeForProduction(sql);
    }

    /**
     * Checks if verbose (development) logging is enabled.
     *
     * @param root the root logger level
     * @param sql the SQL logger level
     * @return true if root or SQL are at DEBUG or TRACE
     */
    public static boolean isDevelopmentMode(String root, String sql) {
        return "DEBUG".equals(root)
                || "TRACE".equals(root)
                || "DEBUG".equals(sql)
                || "TRACE".equals(sql);
    }

    private static boolean isLevelSafeForProduction(String level) {
        return "WARN".equals(level)
                || "ERROR".equals(level)
                || "FATAL".equals(level)
                || "OFF".equals(level);
    }
}
