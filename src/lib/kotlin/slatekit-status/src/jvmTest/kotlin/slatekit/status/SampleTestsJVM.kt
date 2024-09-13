package slatekit.status

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SampleTestsJVM {
    @Test
    fun testHello() {
        assertTrue("JVM" in hello())
    }


    @Test
    fun can_build_status() {
        val status = Passed.Succeeded("Success", 200, "Successful")
        assertEquals(200, status.code)
    }
}