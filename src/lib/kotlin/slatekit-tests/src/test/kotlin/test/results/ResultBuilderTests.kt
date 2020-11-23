package test.results
import org.junit.Test
import slatekit.results.*
import slatekit.results.Codes
import slatekit.results.builders.OutcomeBuilder
import slatekit.results.builders.Outcomes

/**
 * These tests the building/construction of the Result model in simple/advance cases
 * 1. defaults ( no parameters )
 * 2. with message
 * 3. with code
 * 4. with code + message ( TODO )
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
}