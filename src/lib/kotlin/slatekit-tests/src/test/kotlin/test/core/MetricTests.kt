package test.core

import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.ImmutableTag
import io.micrometer.core.instrument.Tag
import io.micrometer.datadog.DatadogConfig
import io.micrometer.datadog.DatadogMeterRegistry
import org.junit.Assert
import org.junit.Test
import java.time.Duration
import java.util.concurrent.ConcurrentLinkedQueue

//import slatekit.providers.metrics.dropwizard.MetricService


class MetricTests {

    //@Test
    fun can_use_datadog() {
        val config = object: DatadogConfig {

            val apikey = "abc"
            val appkey = "abc123"

            override fun apiKey():String = apikey
            override fun applicationKey(): String? = appkey
            override fun step():Duration {
                return Duration.ofSeconds(10)
            }

            override fun get(k:String):String? = null
        }

        val metrics = DatadogMeterRegistry(config, Clock.SYSTEM)
        val queue = mutableListOf<Int>()

        metrics.config().commonTags("env", "dev", "app", "slatekit-tests")
        metrics.counter("api.requests", listOf(ImmutableTag("uri", "/app/user/register")))
        metrics.timer("dat.requests", listOf(ImmutableTag("app", "slatekit-tests")))
        metrics.gauge("que.jobsize",  queue, { it -> it.size.toDouble()})
        queue.add(1)
        queue.add(2)
        Thread.sleep(20000)
        println("done")
    }
}