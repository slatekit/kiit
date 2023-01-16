package test.results
import org.junit.Assert
import org.junit.Test

import kiit.results.*
import kiit.results.Err
import kiit.results.Codes
import kiit.results.builders.Notices
import kiit.results.builders.Outcomes

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
class ResultStatusTests : ResultTestSupport {

    @Test
    fun can_test_success() {
        Assert.assertEquals(true , Codes.SUCCESS.success)
        Assert.assertEquals(true , Codes.PENDING.success)
        Assert.assertEquals(false, Codes.IGNORED.success)
        Assert.assertEquals(false, Codes.BAD_REQUEST.success)
        Assert.assertEquals(false, Codes.UNAUTHENTICATED.success)
        Assert.assertEquals(false, Codes.ERRORED.success)
        Assert.assertEquals(false, Codes.UNEXPECTED.success)
    }

    @Test
    fun can_create_success() {
        val status = Codes.SUCCESS
        ensureSuccess(Success(42), status, 42)
        ensureSuccess(Success(42, "created"), status, 42, "created")
        ensureSuccess(Success(42, "created", 1), status, 42, "created", 1)
        ensureSuccess(Outcomes.pending(42), Codes.PENDING, 42)
    }


    @Test
    fun can_create_failure() {
        val status = Codes.ERRORED
        ensureFailure<Int>(Failure("invalid email"), status, expectedError = "invalid email")
        ensureFailure<Int>(Failure("invalid email", "bad data"), status, expectedStatusMsg = "bad data", expectedError = "invalid email")
        ensureFailure<Int>(Failure("invalid email", -1), status, expectedStatusCode = -1, expectedError = "invalid email")
    }


    @Test
    fun can_create_failure_as_err() {
        val status = Codes.ERRORED
        ensureFailure<Int>(Failure(Err.of("invalid email")), status, expectedError = "invalid email")
        ensureFailure<Int>(Failure(Err.of("invalid email"), "bad data"), status, expectedStatusMsg = "bad data", expectedError = "invalid email")
        ensureFailure<Int>(Failure(Err.of("invalid email"), -1), status, expectedStatusCode = -1, expectedError = "invalid email")
        ensureFailure<Int>(Outcomes.invalid(Err.of("invalid email"), Codes.BAD_REQUEST), Codes.BAD_REQUEST, expectedError = "invalid email")
    }
}