package slatekit.providers.metrics

import io.micrometer.datadog.DatadogConfig
import java.time.Duration

/**
 * Configuration class for datadog
 */
data class DDConfig(val apiKey:String, val appKey:String, val seconds:Int) : DatadogConfig {
    override fun apiKey():String = apiKey

    override fun applicationKey(): String? = appKey

    override fun step(): Duration = Duration.ofSeconds(10)

    override fun get(k:String):String? = null
}