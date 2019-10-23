package test.apis

import org.junit.Assert
import org.junit.Test
import slatekit.apis.core.Api
import slatekit.apis.ApiHost
import slatekit.common.naming.LowerHyphenNamer
import slatekit.common.naming.LowerUnderscoreNamer
import slatekit.results.getOrElse
import test.setup.SampleExtendedApi
import test.setup.SamplePOKOApi


class Api_Naming_Tests : ApiTestsBase() {


    @Test fun can_use_naming_convention_lowerHyphen() {
        val apis = ApiHost(ctx, apis = listOf(Api(SamplePOKOApi::class,
                "app", "SamplePOKO"))
                , auth = null, allowIO = false, namer = LowerHyphenNamer()
        )
        Assert.assertTrue( apis.getApi("app"   , "sample-poko", "get-time"    ).success)
        Assert.assertTrue(!apis.getApi("app"   , "SamplePOKO" , "getTime"      ).success)
        Assert.assertTrue( apis.getApi("app"   , "sample-poko", "get-counter" ).success)
        Assert.assertTrue( apis.getApi("app"   , "sample-poko", "hello"       ).success)
        Assert.assertTrue( apis.getApi("app"   , "sample-poko", "send"     ).success)
        Assert.assertTrue( apis.getApi("app"   , "sample-poko", "response"    ).success)
        Assert.assertTrue(!apis.getApi("app"   , "sample-poko", "get-email"   ).success)
        Assert.assertTrue(!apis.getApi("app"   , "sample-poko", "get-ssn"     ).success)

        val result = apis.call("app", "sample-poko", "get-counter", "", mapOf(), mapOf())
        Assert.assertTrue(result.success)
        Assert.assertTrue(result.getOrElse { 0 } == 1)
    }


    @Test fun can_use_naming_convention_lowerUnderscore() {
        val apis = ApiHost(ctx, apis = listOf(Api(SampleExtendedApi::class,
                "app", "SampleExtended", declaredOnly = false)),
                auth = null, allowIO = false, namer = LowerUnderscoreNamer()
        )
        Assert.assertTrue( apis.getApi("app"   , "sample_extended", "get_seconds" ).success)
        Assert.assertTrue( apis.getApi("app"   , "sample_extended", "get_time"    ).success)
        Assert.assertTrue( apis.getApi("app"   , "sample_extended", "get_counter" ).success)
        Assert.assertTrue( apis.getApi("app"   , "sample_extended", "hello"       ).success)
        Assert.assertTrue( apis.getApi("app"   , "sample_extended", "send"     ).success)
        Assert.assertTrue( apis.getApi("app"   , "sample_extended", "response"    ).success)
        Assert.assertTrue(!apis.getApi("app"   , "sample_extended", "get_email"   ).success)
        Assert.assertTrue(!apis.getApi("app"   , "sample_extended", "get_ssn"     ).success)

        val result = apis.call("app", "sample_extended", "get_seconds", "", mapOf(), mapOf())
        Assert.assertTrue(result.success)
        Assert.assertTrue(result.getOrElse { 0 } in 0..59)
    }
}
