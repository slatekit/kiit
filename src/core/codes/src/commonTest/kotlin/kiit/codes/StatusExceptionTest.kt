package kiit.codes

import kotlin.test.*

class StatusExceptionTest {

    @Test
    fun messageComesFromStatus() {
        val ex = StatusException(Codes.UNAUTHORIZED)
        assertEquals(Codes.UNAUTHORIZED.message, ex.message)
    }

    @Test
    fun statusPropertyIsExactInstance() {
        val ex = StatusException(Codes.TIMEOUT)
        assertSame(Codes.TIMEOUT, ex.status)
    }

    @Test
    fun causeIsNullByDefault() {
        val ex = StatusException(Codes.INVALID)
        assertNull(ex.cause)
    }

    @Test
    fun causeIsChainedWhenProvided() {
        val root = IllegalStateException("root")
        val ex = StatusException(Codes.ERRORED, cause = root)
        assertSame(root, ex.cause)
    }

    @Test
    fun canBeCaughtAsException() {
        val caught = try {
            throw StatusException(Codes.NOT_FOUND)
            null
        } catch (e: StatusException) {
            e
        }
        assertNotNull(caught)
        assertEquals(Codes.NOT_FOUND.name, caught.status.name)
    }

    @Test
    fun worksWithCustomStatus() {
        val custom = Failed.Errored("RATE_LIMITED", 500099, "Rate limited")
        val ex = StatusException(custom)
        assertEquals("Rate limited", ex.message)
        assertSame(custom, ex.status)
    }
}
