
import org.junit.Test
import slatekit.results.*
import slatekit.results.builders.Results
import slatekit.results.StatusCodes
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
        val status = StatusCodes.SUCCESS
        ensureSuccess(success<Int>(), status, null)
        ensureSuccess(success(42), status, 42)
        ensureSuccess(success(42, "life"), status, 42, "life")
        ensureSuccess(success(42, 1), status, 42, null, 1)
        ensureSuccess(success(42, status), status, 42)
    }


    @Test
    fun can_build_pending() {
        val status = StatusCodes.PENDING
        ensureSuccess(pending<Int>(), status, null)
        ensureSuccess(pending(42), status, 42)
        ensureSuccess(pending(42, "life"), status, 42, "life")
        ensureSuccess(pending(42, 1), status, 42, null, 1)
        ensureSuccess(pending(42, status), status, 42)
    }


    @Test
    fun can_build_ignored() {
        val status = StatusCodes.IGNORED
        ensureFailure(ignored<Int>(), status, expectedError = status.msg)
        ensureFailure(ignored<Int>("ignored-x"), status, expectedError = "ignored-x")
        ensureFailure(ignored<Int>(Exception("ignored-x")), status, expectedError = "ignored-x")
        ensureFailure(ignored<Int>(Err.of("ignored-x")), status, expectedError = "ignored-x")
    }


    @Test
    fun can_build_invalid() {
        val status = StatusCodes.INVALID
        ensureFailure(invalid<Int>(), status, expectedError = status.msg)
        ensureFailure(invalid<Int>("invalid-x"), status, expectedError = "invalid-x")
        ensureFailure(invalid<Int>(Exception("invalid-x")), status, expectedError = "invalid-x")
        ensureFailure(invalid<Int>(Err.of("invalid-x")), status, expectedError = "invalid-x")
    }


    @Test
    fun can_build_denied() {
        val status = StatusCodes.DENIED
        ensureFailure(denied<Int>(), status, expectedError = status.msg)
        ensureFailure(denied<Int>("denied-x"), status, expectedError = "denied-x")
        ensureFailure(denied<Int>(Exception("denied-x")), status, expectedError = "denied-x")
        ensureFailure(denied<Int>(Err.of("denied-x")), status, expectedError = "denied-x")
    }


    @Test
    fun can_build_error() {
        val status = StatusCodes.ERRORED
        ensureFailure(errored<Int>(), status, expectedError = status.msg)
        ensureFailure(errored<Int>("error-x"), status, expectedError = "error-x")
        ensureFailure(errored<Int>(Exception("error-x")), status, expectedError = "error-x")
        ensureFailure(errored<Int>(Err.of("error-x")), status, expectedError = "error-x")
    }


    @Test
    fun can_build_unexpected() {
        val status = StatusCodes.UNEXPECTED
        ensureFailure(Outcomes.unexpected<Int>(), status, expectedError = status.msg)
        ensureFailure(Outcomes.unexpected<Int>("unexpected-x"), status, expectedError = "unexpected-x")
        ensureFailure(Outcomes.unexpected<Int>(Exception("unexpected-x")), status, expectedError = "unexpected-x")
        ensureFailure(Outcomes.unexpected<Int>(Err.of("unexpected-x")), status, expectedError = "unexpected-x")
    }
}