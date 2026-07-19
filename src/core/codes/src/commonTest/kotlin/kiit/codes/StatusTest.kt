package kiit.codes

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

// =================================================================================================
// StatusTest — Passed/Failed subtypes, copy helpers, ofCode/ofStatus companion functions
// =================================================================================================

class StatusTest {
    // -------------------------------------------------------------------------
    // success flag — Passed subtypes (hoisted onto Passed itself; see Passed.success)
    // -------------------------------------------------------------------------

    @Test fun succeededHasSuccessTrue() {
        assertTrue(Passed.Succeeded("S", 200001, "S").success)
    }

    @Test fun pendingHasSuccessTrue() {
        assertTrue(Passed.Pending("P", 200101, "P").success)
    }

    @Test fun filteredHasSuccessTrue() {
        assertTrue(Passed.Filtered("F", 200201, "F").success)
    }

    @Test fun informationHasSuccessTrue() {
        assertTrue(Passed.Information("I", 200301, "I").success)
    }

    // -------------------------------------------------------------------------
    // success flag — Failed subtypes (hoisted onto Failed itself; see Failed.success)
    // -------------------------------------------------------------------------

    @Test fun deniedHasSuccessFalse() {
        assertFalse(Failed.Denied("D", 400001, "D").success)
    }

    @Test fun invalidHasSuccessFalse() {
        assertFalse(Failed.Invalid("I", 400102, "I").success)
    }

    @Test fun erroredHasSuccessFalse() {
        assertFalse(Failed.Errored("E", 500005, "E").success)
    }

    @Test fun unserviceableHasSuccessFalse() {
        assertFalse(Failed.Unserviceable("U", 500107, "U").success)
    }

    // -------------------------------------------------------------------------
    // copyMessage — updates message, preserves name and code
    // -------------------------------------------------------------------------

    @Test
    fun copyMessageOnSucceeded() {
        val s = Passed.Succeeded("SUCCESS", 200001, "Success")
        val copy = s.copyMessage("Custom")
        assertEquals("Custom", copy.message)
        assertEquals(s.name, copy.name)
        assertEquals(s.code, copy.code)
        assertTrue(copy.success)
    }

    @Test
    fun copyMessageOnPending() {
        val s = Passed.Pending("PENDING", 200101, "Pending")
        val copy = s.copyMessage("Custom")
        assertEquals("Custom", copy.message)
        assertEquals(s.code, copy.code)
    }

    @Test
    fun copyMessageOnFiltered() {
        val s = Passed.Filtered("SKIPPED", 200201, "Skipped")
        val copy = s.copyMessage("Custom")
        assertEquals("Custom", copy.message)
    }

    @Test
    fun copyMessageOnInformation() {
        val s = Passed.Information("HELP", 200301, "Help")
        val copy = s.copyMessage("Custom")
        assertEquals("Custom", copy.message)
        assertTrue(copy.success)
    }

    @Test
    fun copyMessageOnDenied() {
        val s = Failed.Denied("DENIED", 400001, "Denied")
        val copy = s.copyMessage("Custom")
        assertEquals("Custom", copy.message)
        assertFalse(copy.success)
    }

    @Test
    fun copyMessageOnInvalid() {
        val s = Failed.Invalid("INVALID", 400102, "Invalid")
        val copy = s.copyMessage("Custom")
        assertEquals("Custom", copy.message)
    }

    @Test
    fun copyMessageOnErrored() {
        val s = Failed.Errored("ERRORED", 500005, "Errored")
        val copy = s.copyMessage("Custom")
        assertEquals("Custom", copy.message)
    }

    @Test
    fun copyMessageOnUnserviceable() {
        val s = Failed.Unserviceable("UNEXPECTED", 500107, "Unexpected")
        val copy = s.copyMessage("Custom")
        assertEquals("Custom", copy.message)
        assertFalse(copy.success)
    }

    // -------------------------------------------------------------------------
    // copyAll — updates both message and code
    // -------------------------------------------------------------------------

    @Test
    fun copyAllOnSucceeded() {
        val s = Passed.Succeeded("SUCCESS", 200001, "Success")
        val copy = s.copyAll("Custom", 200099)
        assertEquals("Custom", copy.message)
        assertEquals(200099, copy.code)
        assertEquals(s.name, copy.name)
    }

    @Test
    fun copyAllOnDenied() {
        val s = Failed.Denied("DENIED", 400001, "Denied")
        val copy = s.copyAll("Custom", 403)
        assertEquals("Custom", copy.message)
        assertEquals(403, copy.code)
        assertEquals(s.name, copy.name)
    }

    @Test
    fun copyAllOnUnserviceable() {
        val s = Failed.Unserviceable("TIMEOUT", 500103, "Timeout")
        val copy = s.copyAll("Custom", 408)
        assertEquals("Custom", copy.message)
        assertEquals(408, copy.code)
        assertEquals(s.name, copy.name)
    }

    // -------------------------------------------------------------------------
    // toType — returns lowercase category discriminant, exhaustive over all 8 subtypes
    // -------------------------------------------------------------------------

    @Test
    fun toTypeReturnsCorrectStringForAllSubtypes() {
        assertEquals("succeeded", Status.toType(Passed.Succeeded("S", 1, "S")))
        assertEquals("pending", Status.toType(Passed.Pending("P", 2, "P")))
        assertEquals("filtered", Status.toType(Passed.Filtered("F", 3, "F")))
        assertEquals("information", Status.toType(Passed.Information("N", 4, "N")))
        assertEquals("denied", Status.toType(Failed.Denied("D", 5, "D")))
        assertEquals("invalid", Status.toType(Failed.Invalid("I", 6, "I")))
        assertEquals("errored", Status.toType(Failed.Errored("E", 7, "E")))
        assertEquals("unserviceable", Status.toType(Failed.Unserviceable("U", 8, "U")))
    }

    // -------------------------------------------------------------------------
    // ofCode — returns same instance when nothing changes; applies overrides independently
    // -------------------------------------------------------------------------

    @Test
    fun ofCodeReturnsSameInstanceWhenBothNull() {
        val default = Codes.SUCCESS
        assertSame(default, Status.ofCode(null, null, default))
    }

    @Test
    fun ofCodeReturnsSameInstanceWhenMsgIsEmpty() {
        val default = Codes.SUCCESS
        assertSame(default, Status.ofCode("", null, default))
    }

    @Test
    fun ofCodeReturnsSameInstanceWhenCodeMatchesAndMsgIsNull() {
        val default = Codes.SUCCESS
        assertSame(default, Status.ofCode(null, default.code, default))
    }

    @Test
    fun ofCodeReturnsSameInstanceWhenCodeAndMsgMatchDefault() {
        val default = Codes.SUCCESS
        assertSame(default, Status.ofCode(default.message, default.code, default))
    }

    @Test
    fun ofCodeReturnsCopyWhenMsgDiffers() {
        val default = Codes.SUCCESS
        val result = Status.ofCode("Custom", null, default)
        assertNotSame(default, result)
        assertEquals("Custom", result.message)
        assertEquals(default.code, result.code)
    }

    @Test
    fun ofCodeReturnsCopyWhenCodeDiffers() {
        val default = Codes.SUCCESS
        val result = Status.ofCode(null, 200099, default)
        assertNotSame(default, result)
        assertEquals(200099, result.code)
        assertEquals(default.message, result.message)
    }

    /**
     * Regression test for the original operator-precedence bug:
     *   `if (code == null && msg == null || msg == "") return defaultStatus`
     * bound as `(code == null && msg == null) || (msg == "")`, so any call with msg == ""
     * returned defaultStatus regardless of a supplied code, silently dropping the override.
     */
    @Test
    fun ofCodeAppliesCodeOverrideEvenWhenMsgIsEmptyString() {
        val default = Codes.DENIED
        val result = Status.ofCode("", 401099, default)
        assertNotSame(default, result)
        assertEquals(401099, result.code)
        assertEquals(default.message, result.message)
    }

    // -------------------------------------------------------------------------
    // ofStatus — selects correct instance based on msg / rawStatus nullability
    // -------------------------------------------------------------------------

    @Test
    fun ofStatusReturnStatusWhenBothNull() {
        val status = Codes.SUCCESS
        assertSame(status, Status.ofStatus(null, null, status))
    }

    @Test
    fun ofStatusReturnsRawStatusWhenMsgIsNull() {
        val raw = Codes.CREATED
        val result = Status.ofStatus(null, raw, Codes.SUCCESS)
        assertSame(raw, result)
    }

    @Test
    fun ofStatusReturnsStatusWithUpdatedMsgWhenRawIsNull() {
        val result = Status.ofStatus("Custom", null, Codes.SUCCESS)
        assertEquals("Custom", result.message)
        assertEquals(Codes.SUCCESS.code, result.code)
    }

    @Test
    fun ofStatusReturnsRawWithUpdatedMsgWhenBothProvided() {
        val raw = Codes.CREATED
        val result = Status.ofStatus("Custom", raw, Codes.SUCCESS)
        assertEquals("Custom", result.message)
        assertEquals(raw.code, result.code)
    }
}
