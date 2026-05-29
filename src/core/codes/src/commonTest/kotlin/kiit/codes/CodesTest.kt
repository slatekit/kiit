package kiit.codes

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class CodesTest {
    // -------------------------------------------------------------------------
    // Spot-check a few built-in code values
    // -------------------------------------------------------------------------

    @Test
    fun successHasCorrectValues() {
        assertEquals("SUCCESS", Codes.SUCCESS.name)
        assertEquals(200001, Codes.SUCCESS.code)
        assertEquals("Success", Codes.SUCCESS.message)
        assertTrue(Codes.SUCCESS.success)
    }

    @Test
    fun deniedHasCorrectValues() {
        assertEquals("DENIED", Codes.DENIED.name)
        assertEquals(400005, Codes.DENIED.code)
        assertFalse(Codes.DENIED.success)
    }

    @Test
    fun filteredAndIgnoredHaveSuccessTrue() {
        assertTrue(Codes.FILTERED.success)
        assertTrue(Codes.IGNORED.success)
    }

    // -------------------------------------------------------------------------
    // toHttp — converts a Status to an HTTP status code
    // -------------------------------------------------------------------------

    @Test fun toHttpSuccess() {
        assertEquals(200, Codes.toHttp(Codes.SUCCESS).first)
    }

    @Test fun toHttpCreated() {
        assertEquals(201, Codes.toHttp(Codes.CREATED).first)
    }

    @Test fun toHttpDenied() {
        assertEquals(401, Codes.toHttp(Codes.DENIED).first)
    }

    @Test fun toHttpNotFound() {
        assertEquals(404, Codes.toHttp(Codes.NOT_FOUND).first)
    }

    @Test fun toHttpUnexpected() {
        assertEquals(500, Codes.toHttp(Codes.UNEXPECTED).first)
    }

    @Test
    fun toHttpPreservesOriginalStatus() {
        val (_, status) = Codes.toHttp(Codes.SUCCESS)
        assertSame(Codes.SUCCESS, status)
    }

    @Test
    fun toHttpFallsBackToStatusCodeForUnknownStatus() {
        val custom = Failed.Errored("CUSTOM", 599, "Custom error")
        val (httpCode, _) = Codes.toHttp(custom)
        assertEquals(599, httpCode)
    }

    // -------------------------------------------------------------------------
    // toStatus — reverse lookup by kiit numeric code
    // -------------------------------------------------------------------------

    @Test
    fun toStatusReturnsMatchForKnownCode() {
        val status = Codes.toStatus(Codes.SUCCESS.code)
        assertNotNull(status)
        assertEquals(Codes.SUCCESS.name, status.name)
    }

    @Test
    fun toStatusReturnsNullForUnknownCode() {
        assertNull(Codes.toStatus(99999))
    }

    // -------------------------------------------------------------------------
    // ofCode — maps an HTTP code to a Status, with range-based fallbacks
    // -------------------------------------------------------------------------

    @Test
    fun ofCodeReturnsKnownStatusForRegisteredHttpCode() {
        // 201 is unique in the table so the name is deterministic
        assertEquals(Codes.CREATED.name, Codes.ofCode(201).name)
        // 200/500 have multiple mappings — just verify success flag and type
        assertTrue(Codes.ofCode(200).success)
        assertFalse(Codes.ofCode(500).success)
        assertFalse(Codes.ofCode(404).success)
    }

    @Test
    fun ofCodeFallsBackToSucceededForRange1To999() {
        val status = Codes.ofCode(42)
        assertTrue(status is Passed.Succeeded)
        assertEquals(42, status.code)
    }

    @Test
    fun ofCodeFallsBackToInvalidForRange2000To2999() {
        val status = Codes.ofCode(2001)
        assertTrue(status is Failed.Invalid)
        assertEquals(2001, status.code)
    }

    @Test
    fun ofCodeFallsBackToErroredForCodeAbove3000() {
        val status = Codes.ofCode(9999)
        assertTrue(status is Failed.Errored)
        assertEquals(9999, status.code)
    }

    // -------------------------------------------------------------------------
    // contains — checks whether an HTTP code is in the mapping table
    // -------------------------------------------------------------------------

    @Test
    fun containsReturnsTrueForKnownHttpCode() {
        assertTrue(Codes.contains(200))
        assertTrue(Codes.contains(404))
    }

    @Test
    fun containsReturnsFalseForUnknownCode() {
        assertFalse(Codes.contains(99999))
    }
}
