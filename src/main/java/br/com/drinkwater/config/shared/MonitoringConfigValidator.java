package br.com.drinkwater.config.shared;

/**
 * Pure utility class for evaluating monitoring configurations. Centralizes logic shared between
 * build-time {@code MonitoringProperties} and runtime {@code RuntimeMonitoringConfiguration}.
 */
public final class MonitoringConfigValidator {

    private MonitoringConfigValidator() {}

    /**
     * Checks if monitoring is properly configured for production.
     *
     * @param prometheusEnabled whether Prometheus metrics are enabled
     * @param tracingEnabled whether distributed tracing is enabled
     * @param tracingSamplingRate the tracing sampling rate (0.0 to 1.0)
     * @return true if monitoring is enabled with appropriate settings
     */
    public static boolean isProductionReady(
            boolean prometheusEnabled, boolean tracingEnabled, double tracingSamplingRate) {
        return prometheusEnabled && tracingEnabled && tracingSamplingRate > 0.0;
    }

    /**
     * Validates that tracing configuration is consistent: either tracing is disabled, or it is
     * enabled with a valid endpoint and positive sampling rate.
     *
     * @param tracingEnabled whether tracing is enabled
     * @param zipkinEndpoint the Zipkin endpoint URL
     * @param tracingSamplingRate the sampling rate
     * @return true if tracing is disabled or properly configured
     */
    public static boolean isTracingConfigValid(
            boolean tracingEnabled, String zipkinEndpoint, double tracingSamplingRate) {
        if (!tracingEnabled) {
            return true;
        }
        return zipkinEndpoint != null
                && !zipkinEndpoint.trim().isEmpty()
                && tracingSamplingRate > 0.0;
    }
}
