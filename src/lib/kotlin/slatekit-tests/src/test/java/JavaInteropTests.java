import org.junit.Assert;
import org.junit.Test;
import slatekit.common.results.ResultCode;

public class JavaInteropTests {

    @Test
    public void can_call_kotlin() {
        Assert.assertTrue(ResultCode.BAD_REQUEST == 400);
    }
}
