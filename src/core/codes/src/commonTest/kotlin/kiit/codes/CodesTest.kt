package kiit.codes

import kotlin.test.*

class CodesTest {

    // -------------------------------------------------------------------------
    // Spot-check built-in code values
    // -------------------------------------------------------------------------

    @Test
    fun successHasCorrectValues() {
        assertEquals("SUCCESS", Codes.SUCCESS.name)
        assertEquals(200001,    Codes.SUCCESS.code)
        assertEquals("Success", Codes.SUCCESS.message)
        assertTrue(Codes.SUCCESS.success)
        assertTrue(Codes.SUCCESS is Passed.Succeeded)
    }

    @Test
    fun deniedHasCorrectValues() {
        assertEquals("DENIED", Codes.DENIED.name)
        assertEquals(400005,   Codes.DENIED.code)
        assertFalse(Codes.DENIED.success)
        assertTrue(Codes.DENIED is Failed.Denied)
    }

    @Test
    fun unexpectedHasCorrectValues() {
        assertEquals("UNEXPECTED", Codes.UNEXPECTED.name)
        assertEquals(500008,       Codes.UNEXPECTED.code)
        assertFalse(Codes.UNEXPECTED.success)
        assertTrue(Codes.UNEXPECTED is Failed.Unknown)
    }

    // -------------------------------------------------------------------------
    // All Succeeded codes — success = true, correct sealed type
    // -------------------------------------------------------------------------

    @Test
    fun allSucceededCodesHaveSuccessTrue() {
        val codes = listOf(
            Codes.SUCCESS, Codes.CREATED, Codes.UPDATED, Codes.FETCHED,
            Codes.PATCHED, Codes.DELETED, Codes.HANDLED,
            Codes.EXIT, Codes.HELP, Codes.ABOUT, Codes.VERSION
        )
        codes.forEach {
            assertTrue(it.success,              "${it.name}: expected success=true")
            assertTrue(it is Passed.Succeeded,  "${it.name}: expected Passed.Succeeded")
        }
    }

    // -------------------------------------------------------------------------
    // All Pending codes — success = true, correct sealed type
    // -------------------------------------------------------------------------

    @Test
    fun allPendingCodesHaveSuccessTrue() {
        val codes = listOf(
            Codes.PENDING, Codes.QUEUED, Codes.CONFIRM,
            Codes.ACTIVE, Codes.INACTIVE, Codes.STARTING, Codes.WAITING,
            Codes.RUNNING, Codes.PAUSED, Codes.STOPPED, Codes.COMPLETE
        )
        codes.forEach {
            assertTrue(it.success,            "${it.name}: expected success=true")
            assertTrue(it is Passed.Pending,  "${it.name}: expected Passed.Pending")
        }
    }

    // -------------------------------------------------------------------------
    // Filtered and Ignored — Passed subtypes, success = true
    // -------------------------------------------------------------------------

    @Test
    fun filteredIsPassedWithSuccessTrue() {
        assertTrue(Codes.FILTERED.success)
        assertTrue(Codes.FILTERED is Passed.Filtered)
    }

    @Test
    fun ignoredIsPassedWithSuccessTrue() {
        assertTrue(Codes.IGNORED.success)
        assertTrue(Codes.IGNORED is Passed.Ignored)
    }

    // -------------------------------------------------------------------------
    // All Denied codes — success = false, correct sealed type
    // -------------------------------------------------------------------------

    @Test
    fun allDeniedCodesHaveSuccessFalse() {
        val codes = listOf(
            Codes.DENIED, Codes.UNSUPPORTED, Codes.UNIMPLEMENTED,
            Codes.UNAVAILABLE, Codes.UNAUTHENTICATED, Codes.UNAUTHORIZED
        )
        codes.forEach {
            assertFalse(it.success,           "${it.name}: expected success=false")
            assertTrue(it is Failed.Denied,   "${it.name}: expected Failed.Denied")
        }
    }

    // -------------------------------------------------------------------------
    // All Invalid codes — success = false, correct sealed type
    // -------------------------------------------------------------------------

    @Test
    fun allInvalidCodesHaveSuccessFalse() {
        val codes = listOf(Codes.BAD_REQUEST, Codes.INVALID, Codes.NOT_FOUND)
        codes.forEach {
            assertFalse(it.success,           "${it.name}: expected success=false")
            assertTrue(it is Failed.Invalid,  "${it.name}: expected Failed.Invalid")
        }
    }

    // -------------------------------------------------------------------------
    // All Errored codes — success = false, correct sealed type
    // -------------------------------------------------------------------------

    @Test
    fun allErroredCodesHaveSuccessFalse() {
        val codes = listOf(
            Codes.MISSING, Codes.FORBIDDEN, Codes.CONFLICT, Codes.DEPRECATED,
            Codes.TIMEOUT, Codes.ERRORED, Codes.LIMITED
        )
        codes.forEach {
            assertFalse(it.success,           "${it.name}: expected success=false")
            assertTrue(it is Failed.Errored,  "${it.name}: expected Failed.Errored")
        }
    }

    // -------------------------------------------------------------------------
    // toHttp — known codes map to correct HTTP status
    // -------------------------------------------------------------------------

    @Test fun toHttpSuccess()     { assertEquals(200, Codes.toHttp(Codes.SUCCESS).first) }
    @Test fun toHttpCreated()     { assertEquals(201, Codes.toHttp(Codes.CREATED).first) }
    @Test fun toHttpPending()     { assertEquals(202, Codes.toHttp(Codes.PENDING).first) }
    @Test fun toHttpHandled()     { assertEquals(204, Codes.toHttp(Codes.HANDLED).first) }
    @Test fun toHttpDenied()      { assertEquals(401, Codes.toHttp(Codes.DENIED).first) }
    @Test fun toHttpUnauthorized(){ assertEquals(401, Codes.toHttp(Codes.UNAUTHORIZED).first) }
    @Test fun toHttpNotFound()    { assertEquals(404, Codes.toHttp(Codes.NOT_FOUND).first) }
    @Test fun toHttpTimeout()     { assertEquals(408, Codes.toHttp(Codes.TIMEOUT).first) }
    @Test fun toHttpConflict()    { assertEquals(409, Codes.toHttp(Codes.CONFLICT).first) }
    @Test fun toHttpForbidden()   { assertEquals(403, Codes.toHttp(Codes.FORBIDDEN).first) }
    @Test fun toHttpUnsupported() { assertEquals(501, Codes.toHttp(Codes.UNSUPPORTED).first) }
    @Test fun toHttpUnavailable() { assertEquals(503, Codes.toHttp(Codes.UNAVAILABLE).first) }
    @Test fun toHttpUnexpected()  { assertEquals(500, Codes.toHttp(Codes.UNEXPECTED).first) }

    @Test
    fun toHttpPreservesOriginalStatus() {
        val (_, status) = Codes.toHttp(Codes.SUCCESS)
        assertSame(Codes.SUCCESS, status)
    }

    @Test
    fun toHttpFallsBackToStatusCodeForUnknownStatus() {
        val custom = Failed.Errored("CUSTOM", 599, "Custom error")
        val (httpCode, status) = Codes.toHttp(custom)
        assertEquals(599,    httpCode)
        assertSame(custom,   status)
    }

    // -------------------------------------------------------------------------
    // toStatus — reverse lookup by numeric code
    // -------------------------------------------------------------------------

    @Test
    fun toStatusReturnsStatusForKnownCode() {
        val status = Codes.toStatus(Codes.SUCCESS.code)
        assertNotNull(status)
        assertEquals(Codes.SUCCESS.name, status.name)
    }

    @Test
    fun toStatusReturnsNullForUnknownCode() {
        assertNull(Codes.toStatus(99999))
    }

    // -------------------------------------------------------------------------
    // ofCode — maps numeric code to Status, with range-based fallbacks
    // -------------------------------------------------------------------------

    @Test
    fun ofCodeReturnsKnownStatusForRegisteredCode() {
        val status = Codes.ofCode(Codes.SUCCESS.code)
        assertEquals(Codes.SUCCESS.name, status.name)
    }

    @Test
    fun ofCodeReturnsSucceededForCodeInRange1To999() {
        val status = Codes.ofCode(42)
        assertTrue(status is Passed.Succeeded)
        assertEquals(Codes.SUCCESS.name, status.name)
        assertEquals(42, status.code)
    }

    @Test
    fun ofCodeReturnsInvalidForCodeInRange2000To2999() {
        val status = Codes.ofCode(2001)
        assertTrue(status is Failed.Invalid)
        assertEquals(Codes.INVALID.name, status.name)
        assertEquals(2001, status.code)
    }

    @Test
    fun ofCodeReturnsErroredForCodeAtOrAbove3000() {
        val status = Codes.ofCode(9999)
        assertTrue(status is Failed.Errored)
        assertEquals(Codes.ERRORED.name, status.name)
        assertEquals(9999, status.code)
    }

    @Test
    fun ofCodeReturnsErroredForUnmappedNegativeOrZeroCode() {
        val status = Codes.ofCode(-1)
        assertTrue(status is Failed.Errored)
    }

    // -------------------------------------------------------------------------
    // contains — checks whether an HTTP code is in the lookup table
    // -------------------------------------------------------------------------

    @Test
    fun containsReturnsTrueForKnownHttpCode() {
        assertTrue(Codes.contains(200))
        assertTrue(Codes.contains(404))
        assertTrue(Codes.contains(500))
    }

    @Test
    fun containsReturnsFalseForUnknownHttpCode() {
        assertFalse(Codes.contains(99999))
        assertFalse(Codes.contains(0))
    }
}
