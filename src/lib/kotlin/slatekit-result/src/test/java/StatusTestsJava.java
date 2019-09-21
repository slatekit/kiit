import org.junit.Assert;
import org.junit.Test;
import slatekit.results.Status;
import slatekit.results.Codes;

public class StatusTestsJava {

    @Test
    public void confirm_success_codes() {
        checkCode(Codes.SUCCESS, 200001, "Success");
    }


    @Test
    public void confirm_filter_codes() {
        checkCode(Codes.IGNORED, 400001, "Ignored");
    }


    @Test
    public void confirm_invalid_codes() {
        checkCode(Codes.INVALID, 400003, "Invalid");
    }


    @Test
    public void confirm_errored_codes() {
        checkCode(Codes.ERRORED, 500007, "Errored");
    }


    private void checkCode(Status status, int expectedCode, String expectedMsg){
        Assert.assertEquals(status.getCode(), expectedCode);
        Assert.assertEquals(status.getMsg(), expectedMsg);
    }
}
