import org.junit.Assert;
import org.junit.Test;
import slatekit.common.Types;
import slatekit.common.info.Host;
import slatekit.common.results.ResultCode;

public class JavaInteropTests {

    @Test
    public void can_call_kotlin() {

        Host host = new Host("mac1", "127.0.0.1", "", "osx", "10", "");
        host.copy(host.name, host.ip, host.origin, host.arch, host.version, host.ext1);

        Assert.assertTrue(ResultCode.BAD_REQUEST == 400);
    }
}
