package slatekit.status

import kotlin.test.Test
import kotlin.test.assertTrue

class SampleTestsJVM {
    @Test
    fun testHello() {
        assertTrue("JVM" in hello())
    }
}