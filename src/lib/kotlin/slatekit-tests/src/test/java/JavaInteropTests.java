import org.junit.Assert;
import org.junit.Test;
import slatekit.common.ApiLogin;
import slatekit.common.DateTime;
import slatekit.common.results.ResultCode;
import slatekit.meta.models.ModelField;

public class JavaInteropTests {

    @Test
    public void can_call_kotlin() {
        Assert.assertTrue(ResultCode.BAD_REQUEST == 400);
    }
}
