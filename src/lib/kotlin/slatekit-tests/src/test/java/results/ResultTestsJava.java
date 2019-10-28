/*
public class ResultTestsJava {

    @Test
    public void can_build_successful_result() {
        ensureSuccess(success(1), Codes.SUCCESS, 1, null);
    }


    @Test
    public void can_build_successful_result_with_custom_message() {
        ensureSuccess(success(1, "created user"), Codes.SUCCESS, 1, "created user");
    }


    @Test
    public void can_build_successful_result_with_custom_message_and_code() {
        ensureSuccess(success(1, "created user"), Codes.SUCCESS, 1, "created user");
    }


    @Test
    public void can_build_filtered_error() {
        ensureFailure(ignored(), Codes.IGNORED, null);
    }


    @Test
    public void can_build_filtered_error_with_custom_message() {
        ensureFailure(ignored("event category filtered out"), Codes.IGNORED, "event category filtered out");
    }


    @Test
    public void can_build_invalid_error() {
        ensureFailure(invalid(), Codes.INVALID, null);
    }


    @Test
    public void can_build_invalid_error_with_custom_message() {
        ensureFailure(invalid("event category not supplied"), Codes.INVALID, "event category not supplied");
    }


    @Test
    public void can_build_errored_error() {
        ensureFailure(errored(), Codes.ERRORED, null);
    }


    @Test
    public void can_build_errored_error_with_custom_message() {
        ensureFailure(errored("event category unexpected"), Codes.ERRORED, "event category unexpected");
    }


    @Test
    public void can_build_unexpected_error() {
        ensureFailure(unexpected(), Codes.UNEXPECTED, null);
    }


    @Test
    public void can_build_unexpected_error_with_custom_message() {
        ensureFailure(unexpected("unable to save"), Codes.UNEXPECTED, "unable to save");
    }


    private <T> void ensureSuccess(Result<T, Err> result, Status expectedStatus, T expectedValue, String expectedMessage) {
        String finalMessage = expectedMessage == null ? expectedStatus.getMsg() : expectedMessage;

        Assert.assertEquals(true, result.getSuccess());
        Assert.assertEquals(expectedStatus.getCode(), result.getCode());
        Assert.assertEquals(finalMessage, result.getMsg());

        result.onSuccess(value -> {
            Assert.assertEquals(value, expectedValue);
            return Unit.INSTANCE;
        });
    }


    private <T> void ensureFailure(Result<T, Err> result, Status expectedStatus, String expectedMessage) {
        String finalMessage = expectedMessage == null ? expectedStatus.getMsg() : expectedMessage;

        Assert.assertEquals(false, result.getSuccess());
        Assert.assertEquals(expectedStatus.getCode(), result.getCode());
        Assert.assertEquals(finalMessage, result.getMsg());
    }
}
*/