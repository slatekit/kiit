package test.common

import org.junit.Test
import slatekit.core.gate.*


class MonitorTests {

    @Test
    fun can_setup() {
        val sample = MonitorSample()
        sample.run
    }


    class MonitorSample : Gated {

    }
}