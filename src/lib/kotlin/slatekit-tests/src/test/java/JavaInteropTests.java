import kotlin.Unit;
import org.junit.Assert;
import org.junit.Test;
import slatekit.common.Random;
import slatekit.common.Result;
import slatekit.common.Success;
import slatekit.common.envs.Env;
import slatekit.common.envs.EnvMode;
import slatekit.common.envs.Envs;
import slatekit.common.info.Host;
import slatekit.common.results.ResultCode;

import java.util.ArrayList;
import java.util.List;


public class JavaInteropTests {

    @Test
    public void can_call_kotlin() {

        Host host = new Host("mac1", "127.0.0.1", "", "osx", "10", "");
        host.copy(host.name, host.ip, host.origin, host.arch, host.version, host.ext1);

        Assert.assertTrue(ResultCode.BAD_REQUEST == 400);
    }


    @Test
    public void can_use_result() {

        Result<Integer, Exception> resS1 = new Success<Integer>(3, ResultCode.SUCCESS, "");
        //Result<Integer, Exception> resS2 = success2(3, ResultCode.SUCCESS, "");
    }


    @Test
    public void can_test_env(){
        List<Env> all = new ArrayList<Env>();
        all.add( new Env("loc", EnvMode.parse("Dev"), "","Dev environment (local)"));
        all.add( new Env("dv1", EnvMode.parse("Dev"), "","Dev 1 environment (shared)"));
        all.add( new Env("dv2", EnvMode.parse("Dev"), "","Dev 2 environment (shared)"));
        all.add( new Env("qa1", EnvMode.parse("Qat"), "","QA environment  (current release)"));
        all.add( new Env("qa2", EnvMode.parse("Qat"), "","QA environment  (last release)"));
        all.add( new Env("stg", EnvMode.parse("Uat"), "","STG environment (demo)"));
        all.add( new Env("pro", EnvMode.parse("Pro"), "","LIVE environment"));

        Envs envs = new Envs(all).select("dv1");

        System.out.println("ALL");
        System.out.println(envs.isDev());
        System.out.println(envs.getName());
        System.out.println(envs.getEnv());
        System.out.println(envs.getKey());

        System.out.println("CURRENT");
        System.out.println(envs.getCurrent().name);
        System.out.println(envs.getCurrent().mode.getName());
        System.out.println(envs.getCurrent().getKey());
        System.out.println(envs.getCurrent().region);
        System.out.println(envs.getCurrent().desc);
    }
}
