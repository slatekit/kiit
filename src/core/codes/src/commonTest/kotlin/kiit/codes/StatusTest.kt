package kiit.codes

import kotlin.test.*

class StatusTest {

    // -------------------------------------------------------------------------
    // success flag — Passed subtypes
    // -------------------------------------------------------------------------

    @Test fun succeededHasSuccessTrue()  { assertTrue(Passed.Succeeded("S", 200001, "S").success) }
    @Test fun pendingHasSuccessTrue()    { assertTrue(Passed.Pending  ("P", 200008, "P").success) }
    @Test fun filteredHasSuccessTrue()   { assertTrue(Passed.Filtered ("F", 200204, "F").success) }
    @Test fun ignoredHasSuccessTrue()    { assertTrue(Passed.Ignored  ("I", 200204, "I").success) }

    // -------------------------------------------------------------------------
    // success flag — Failed subtypes
    // -------------------------------------------------------------------------

    @Test fun deniedHasSuccessFalse()    { assertFalse(Failed.Denied ("D", 400005, "D").success) }
    @Test fun invalidHasSuccessFalse()   { assertFalse(Failed.Invalid("I", 400003, "I").success) }
    @Test fun erroredHasSuccessFalse()   { assertFalse(Failed.Errored("E", 500007, "E").success) }
    @Test fun unknownHasSuccessFalse()   { assertFalse(Failed.Unknown("U", 500008, "U").success) }

    // -------------------------------------------------------------------------
    // copyMessage — updates message, preserves name and code
    // -------------------------------------------------------------------------

    @Test
    fun copyMessageOnSucceeded() {
        val s = Passed.Succeeded("SUCCESS", 200001, "Success")
        val copy = s.copyMessage("Custom")
        assertEquals("Custom",  copy.message)
        assertEquals(s.name,    copy.name)
        assertEquals(s.code,    copy.code)
        assertTrue(copy.success)
    }

    @Test
    fun copyMessageOnPending() {
        val s = Passed.Pending("PENDING", 200008, "Pending")
        val copy = s.copyMessage("Custom")
        assertEquals("Custom", copy.message)
        assertEquals(s.code,   copy.code)
    }

    @Test
    fun copyMessageOnFiltered() {
        val s = Passed.Filtered("FILTERED", 200204, "Filtered")
        val copy = s.copyMessage("Custom")
        assertEquals("Custom", copy.message)
    }

    @Test
    fun copyMessageOnIgnored() {
        val s = Passed.Ignored("IGNORED", 200204, "Ignored")
        val copy = s.copyMessage("Custom")
        assertEquals("Custom", copy.message)
    }

    @Test
    fun copyMessageOnDenied() {
        val s = Failed.Denied("DENIED", 400005, "Denied")
        val copy = s.copyMessage("Custom")
        assertEquals("Custom", copy.message)
        assertFalse(copy.success)
    }

    @Test
    fun copyMessageOnInvalid() {
        val s = Failed.Invalid("INVALID", 400003, "Invalid")
        val copy = s.copyMessage("Custom")
        assertEquals("Custom", copy.message)
    }

    @Test
    fun copyMessageOnErrored() {
        val s = Failed.Errored("ERRORED", 500007, "Errored")
        val copy = s.copyMessage("Custom")
        assertEquals("Custom", copy.message)
    }

    @Test
    fun copyMessageOnUnknown() {
        val s = Failed.Unknown("UNKNOWN", 500008, "Unknown")
        val copy = s.copyMessage("Custom")
        assertEquals("Custom", copy.message)
    }

    // -------------------------------------------------------------------------
    // copyAll — updates both message and code
    // -------------------------------------------------------------------------

    @Test
    fun copyAllOnSucceeded() {
        val s = Passed.Succeeded("SUCCESS", 200001, "Success")
        val copy = s.copyAll("Custom", 200099)
        assertEquals("Custom", copy.message)
        assertEquals(200099,   copy.code)
        assertEquals(s.name,   copy.name)
    }

    @Test
    fun copyAllOnDenied() {
        val s = Failed.Denied("DENIED", 400005, "Denied")
        val copy = s.copyAll("Custom", 403)
        assertEquals("Custom", copy.message)
        assertEquals(403,      copy.code)
        assertEquals(s.name,   copy.name)
    }

    // -------------------------------------------------------------------------
    // toType — returns lowercase discriminant string
    // -------------------------------------------------------------------------

    @Test
    fun toTypeReturnsCorrectStringForAllSubtypes() {
        assertEquals("succeeded", Status.toType(Passed.Succeeded("S", 1, "S")))
        assertEquals("pending",   Status.toType(Passed.Pending  ("P", 2, "P")))
        assertEquals("filtered",  Status.toType(Passed.Filtered ("F", 3, "F")))
        assertEquals("ignored",   Status.toType(Passed.Ignored  ("I", 4, "I")))
        assertEquals("denied",    Status.toType(Failed.Denied   ("D", 5, "D")))
        assertEquals("invalid",   Status.toType(Failed.Invalid  ("I", 6, "I")))
        assertEquals("errored",   Status.toType(Failed.Errored  ("E", 7, "E")))
        assertEquals("unknown",   Status.toType(Failed.Unknown  ("U", 8, "U")))
    }

    // -------------------------------------------------------------------------
    // ofCode — returns same instance when nothing changes (optimisation)
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
        assertEquals("Custom",      result.message)
        assertEquals(default.code,  result.code)
    }

    @Test
    fun ofCodeReturnsCopyWhenCodeDiffers() {
        val default = Codes.SUCCESS
        val result = Status.ofCode(null, 200099, default)
        assertNotSame(default, result)
        assertEquals(200099,           result.code)
        assertEquals(default.message,  result.message)
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
        assertEquals("Custom",          result.message)
        assertEquals(Codes.SUCCESS.code, result.code)
    }

    @Test
    fun ofStatusReturnsRawWithUpdatedMsgWhenBothProvided() {
        val raw = Codes.CREATED
        val result = Status.ofStatus("Custom", raw, Codes.SUCCESS)
        assertEquals("Custom",    result.message)
        assertEquals(raw.code,    result.code)
    }
}
