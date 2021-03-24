package test.results
import org.junit.Assert
import org.junit.Test
import slatekit.meta.kClass
import slatekit.results.*
import slatekit.results.Codes
import slatekit.results.builders.OutcomeBuilder
import slatekit.results.builders.Outcomes
import slatekit.serialization.responses.ResponseDecoder

/**
 * These tests the building/construction of the Result model in simple/advance cases
 * 1. defaults ( no parameters )
 * 2. with message
 * 3. with code
 * 4. with code + message
 */
class ResultBuilderTests : ResultTestSupport, OutcomeBuilder {

    @Test
    fun can_build_successes() {
        val status = Codes.SUCCESS
        ensureSuccess(success<Int>(), status, null)
        ensureSuccess(success(42), status, 42)
        ensureSuccess(success(42, "life"), status, 42, "life")
        ensureSuccess(success(42, 1), status, 42, null, 1)
        ensureSuccess(success(42, status), status, 42)
    }


    @Test
    fun can_build_pending() {
        val status = Codes.PENDING
        ensureSuccess(pending<Int>(), status, null)
        ensureSuccess(pending(42), status, 42)
        ensureSuccess(pending(42, "life"), status, 42, "life")
        ensureSuccess(pending(42, 1), status, 42, null, 1)
        ensureSuccess(pending(42, status), status, 42)
    }


    @Test
    fun can_build_ignored() {
        val status = Codes.IGNORED
        ensureFailure(ignored<Int>(), status, expectedError = status.desc)
        ensureFailure(ignored<Int>("ignored-x"), status, expectedError = "ignored-x")
        ensureFailure(ignored<Int>(Exception("ignored-x")), status, expectedError = "ignored-x")
        ensureFailure(ignored<Int>(Err.of("ignored-x")), status, expectedError = "ignored-x")
    }


    @Test
    fun can_build_invalid() {
        val status = Codes.INVALID
        ensureFailure(invalid<Int>(), status, expectedError = status.desc)
        ensureFailure(invalid<Int>("invalid-x"), status, expectedError = "invalid-x")
        ensureFailure(invalid<Int>(Exception("invalid-x")), status, expectedError = "invalid-x")
        ensureFailure(invalid<Int>(Err.of("invalid-x")), status, expectedError = "invalid-x")
    }


    @Test
    fun can_build_denied() {
        val status = Codes.DENIED
        ensureFailure(denied<Int>(), status, expectedError = status.desc)
        ensureFailure(denied<Int>("denied-x"), status, expectedError = "denied-x")
        ensureFailure(denied<Int>(Exception("denied-x")), status, expectedError = "denied-x")
        ensureFailure(denied<Int>(Err.of("denied-x")), status, expectedError = "denied-x")
    }


    @Test
    fun can_build_error() {
        val status = Codes.ERRORED
        ensureFailure(errored<Int>(), status, expectedError = status.desc)
        ensureFailure(errored<Int>("error-x"), status, expectedError = "error-x")
        ensureFailure(errored<Int>(Exception("error-x")), status, expectedError = "error-x")
        ensureFailure(errored<Int>(Err.of("error-x")), status, expectedError = "error-x")
    }


    @Test
    fun can_build_unexpected() {
        val status = Codes.UNEXPECTED
        ensureFailure(Outcomes.unexpected<Int>(), status, expectedError = status.desc)
        ensureFailure(Outcomes.unexpected<Int>("unexpected-x"), status, expectedError = "unexpected-x")
        ensureFailure(Outcomes.unexpected<Int>(Exception("unexpected-x")), status, expectedError = "unexpected-x")
        ensureFailure(Outcomes.unexpected<Int>(Err.of("unexpected-x")), status, expectedError = "unexpected-x")
    }


    @Test
    fun can_parse_error() {
        val json = """
            {
            	"errs": [
                    {
                        "field": "email",
                        "type": "input",
                        "message": "Email already exists",
                        "value": "slate.kit@gmail.com"
            	    },
                    {
                        "field": "",
                        "type": "action",
                        "message": "Unable to register user",
                        "value": ""
            	    }
                ],
            	"code": 500004,
            	"success": false,
            	"meta": null,
            	"name": "CONFLICT",
                "type": "Errored",
            	"tag": null,
            	"value": null,
            	"desc": "Conflict"
            }
        """.trimIndent()
        val result = ResponseDecoder.outcome(json, Int::class.java) { Outcomes.success(42) }
        Assert.assertFalse(result.success)
        Assert.assertEquals( Codes.CONFLICT.name, result.status.name)
        Assert.assertEquals( Codes.CONFLICT.desc, result.status.desc)
        Assert.assertEquals( Codes.CONFLICT.code, result.status.code)
        result.onFailure {err ->
            Assert.assertTrue( err is Err.ErrorList)
            Assert.assertTrue( (err as Err.ErrorList).errors[0] is Err.ErrorField)
            Assert.assertTrue( (err as Err.ErrorList).errors[1] is Err.ErrorInfo)
            val errs = err as Err.ErrorList

            val err1 = errs.errors[0] as Err.ErrorField
            Assert.assertEquals("email", err1.field)
            Assert.assertEquals("slate.kit@gmail.com", err1.value)
            Assert.assertEquals("Email already exists", err1.msg)

            val err2 = errs.errors[1] as Err.ErrorInfo
            Assert.assertEquals("Unable to register user", err2.msg)
        }
    }

    @Test
    fun can_parse_value_using_default_code() {
        val json = """
            {
            	"success": true,
            	"name": "SUCCESS",
            	"type": "Succeeded",
            	"code": 200001,
            	"desc": "Successful operation",
            	"value": 42,
            	"errs": null,
            	"meta": null,
            	"tag": null
            }
        """.trimIndent()
        val result = ResponseDecoder.outcome(json, Int::class.java) { 42 }
        Assert.assertTrue(result.success)
        Assert.assertTrue(result.status is Passed.Succeeded)
        Assert.assertEquals(42, result.getOrNull())
        Assert.assertEquals(Codes.SUCCESS.name, result.status.name)
        Assert.assertEquals(Codes.SUCCESS.code, result.status.code)
        Assert.assertEquals(Codes.SUCCESS.desc, result.status.desc)
    }

    @Test
    fun can_parse_value_using_custom_code() {
        val json = """
            {
            	"success": true,
            	"name": "REGISTERED",
            	"type": "Succeeded",
            	"code": 900001,
            	"desc": "Registration successful",
            	"value": 42,
            	"errs": null,
            	"meta": null,
            	"tag": null
            }
        """.trimIndent()
        val result = ResponseDecoder.outcome(json, Int::class.java) { 42 }
        Assert.assertTrue(result.success)
        Assert.assertTrue(result.status is Passed.Succeeded)
        Assert.assertEquals(42, result.getOrNull())
        Assert.assertEquals("REGISTERED", result.status.name)
        Assert.assertEquals(900001, result.status.code)
        Assert.assertEquals("Registration successful", result.status.desc)
    }


    @Test
    fun can_get_type_of_status() {
        Assert.assertEquals("Succeeded" , Status.toType(Codes.SUCCESS))
        Assert.assertEquals("Pending"   , Status.toType(Codes.PENDING))
        Assert.assertEquals("Denied"    , Status.toType(Codes.DENIED))
        Assert.assertEquals("Ignored"   , Status.toType(Codes.IGNORED))
        Assert.assertEquals("Invalid"   , Status.toType(Codes.INVALID))
        Assert.assertEquals("Errored"   , Status.toType(Codes.ERRORED))
        Assert.assertEquals("Unknown"   , Status.toType(Codes.UNEXPECTED))
    }
}