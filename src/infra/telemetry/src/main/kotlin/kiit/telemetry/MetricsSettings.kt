package kiit.telemetry

data class MetricsSettings(
        val enabled:Boolean,
        val standardize: Boolean,
        val tags: Tags)
