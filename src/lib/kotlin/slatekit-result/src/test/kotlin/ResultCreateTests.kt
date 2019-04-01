
import org.junit.Test

import slatekit.results.*
import slatekit.results.Err
import slatekit.results.StatusCodes

/**
 * Tests Operations on the Result class which include:
 * 1. map
 * 2. flatMap
 * 3. onSuccess
 * 4. onFailure
 * 5. fold
 *
 * The tests check the operations above on both:
 * 1. Success branch
 * 2. Failure branch
 */
class ResultCreateTests : ResultTestSupport {

    @Test
    fun can_create_success() {
        val status = StatusCodes.SUCCESS
        ensureSuccess(Success(42), status, 42)
        ensureSuccess(Success(42, "created"), status, 42, "created")
        ensureSuccess(Success(42, "created", 1), status, 42, "created", 1)
        ensureSuccess(Success(42, StatusCodes.PENDING), StatusCodes.PENDING, 42)
    }


    @Test
    fun can_create_failure() {
        val status = StatusCodes.ERRORED
        ensureFailure<Int>(Failure("invalid email"), status, expectedError = "invalid email")
        ensureFailure<Int>(Failure("invalid email", "bad data"), status, expectedStatusMsg = "bad data", expectedError = "invalid email")
        ensureFailure<Int>(Failure("invalid email", -1), status, expectedStatusCode = -1, expectedError = "invalid email")
        ensureFailure<Int>(Failure("invalid email", StatusCodes.BAD_REQUEST), StatusCodes.BAD_REQUEST, expectedError = "invalid email")
    }


    @Test
    fun can_create_failure_as_err() {
        val status = StatusCodes.ERRORED
        ensureFailure<Int>(Failure(Err.of("invalid email")), status, expectedError = "invalid email")
        ensureFailure<Int>(Failure(Err.of("invalid email"), "bad data"), status, expectedStatusMsg = "bad data", expectedError = "invalid email")
        ensureFailure<Int>(Failure(Err.of("invalid email"), -1), status, expectedStatusCode = -1, expectedError = "invalid email")
        ensureFailure<Int>(Failure(Err.of("invalid email"), StatusCodes.BAD_REQUEST), StatusCodes.BAD_REQUEST, expectedError = "invalid email")
    }
}