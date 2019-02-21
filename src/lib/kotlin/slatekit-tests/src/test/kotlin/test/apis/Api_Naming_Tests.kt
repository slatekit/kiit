package test.apis

import org.junit.Test
import slatekit.apis.core.Api
import slatekit.apis.ApiContainer
import slatekit.common.naming.LowerHyphenNamer
import slatekit.common.naming.LowerUnderscoreNamer
import slatekit.results.getOrElse
import test.setup.SampleExtendedApi
import test.setup.SamplePOKOApi


class Api_Naming_Tests : ApiTestsBase() {


    @Test fun can_use_naming_convention_lowerHyphen() {
        val apis = ApiContainer(ctx, apis = listOf(Api(SamplePOKOApi::class,
                "app", "SamplePOKO"))
                , auth = null, allowIO = false, namer = LowerHyphenNamer()
        )
        assert( apis.getApi("app"   , "sample-poko", "get-time"    ).success)
        assert(!apis.getApi("app"   , "SamplePOKO" , "getTime"      ).success)
        assert( apis.getApi("app"   , "sample-poko", "get-counter" ).success)
        assert( apis.getApi("app"   , "sample-poko", "hello"       ).success)
        assert( apis.getApi("app"   , "sample-poko", "request"     ).success)
        assert( apis.getApi("app"   , "sample-poko", "response"    ).success)
        assert(!apis.getApi("app"   , "sample-poko", "get-email"   ).success)
        assert(!apis.getApi("app"   , "sample-poko", "get-ssn"     ).success)

        val result = apis.call("app", "sample-poko", "get-counter", "", mapOf(), mapOf())
        assert(result.success)
        assert(result.getOrElse { 0 } == 1)
    }


    @Test fun can_use_naming_convention_lowerUnderscore() {
        val apis = ApiContainer(ctx, apis = listOf(Api(SampleExtendedApi::class,
                "app", "SampleExtended", declaredOnly = false)),
                auth = null, allowIO = false, namer = LowerUnderscoreNamer()
        )
        assert( apis.getApi("app"   , "sample_extended", "get_seconds" ).success)
        assert( apis.getApi("app"   , "sample_extended", "get_time"    ).success)
        assert( apis.getApi("app"   , "sample_extended", "get_counter" ).success)
        assert( apis.getApi("app"   , "sample_extended", "hello"       ).success)
        assert( apis.getApi("app"   , "sample_extended", "request"     ).success)
        assert( apis.getApi("app"   , "sample_extended", "response"    ).success)
        assert(!apis.getApi("app"   , "sample_extended", "get_email"   ).success)
        assert(!apis.getApi("app"   , "sample_extended", "get_ssn"     ).success)

        val result = apis.call("app", "sample_extended", "get_seconds", "", mapOf(), mapOf())
        assert(result.success)
        assert(result.getOrElse { 0 } in 0..59)
    }
}
