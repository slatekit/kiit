package test.results
import org.junit.Assert
import slatekit.results.*
import slatekit.results.Status

interface ResultTestSupport {


    fun <T> ensureSuccess(
        result: Result<T, Err>,
        expectedStatus: Status,
        expectedValue: T,
        expectedMessage: String? = null,
        expectedCode: Int? = null
    ) {
        Assert.assertEquals( result.success, true)
        Assert.assertEquals( result.status.code, (expectedCode ?: expectedStatus.code))
        Assert.assertEquals( result.status.msg , (expectedMessage ?: expectedStatus.msg))
        Assert.assertEquals( result.code , (expectedCode ?: expectedStatus.code))
        Assert.assertEquals( result.msg , (expectedMessage ?: expectedStatus.msg))

        result.onSuccess { value ->
            Assert.assertEquals(expectedValue, value)
        }
    }


    fun <T> ensureFailure(
        result: Result<T, *>,
        expectedStatus: Status,
        expectedStatusMsg: String? = null,
        expectedStatusCode: Int? = null,
        expectedError: String? = null
    ) {
        Assert.assertEquals(result.success , false)
        Assert.assertEquals(result.status.code , (expectedStatusCode ?: expectedStatus.code))
        Assert.assertEquals(result.status.msg , (expectedStatusMsg ?: expectedStatus.msg))
        Assert.assertEquals(result.code , (expectedStatusCode ?: expectedStatus.code))
        Assert.assertEquals(result.msg , (expectedStatusMsg ?: expectedStatus.msg))
        result.onFailure {
            when (it) {
                is String    -> Assert.assertEquals(it , expectedError)
                is ErrorInfo -> Assert.assertEquals(it.msg , expectedError)
                else                  -> throw Exception("Unexpected for : $it")
            }
        }
    }
}