package test

import org.junit.Test
import slatekit.apis.ApiReg
import slatekit.apis.ApiContainer
import slatekit.common.Namer
import slatekit.common.lowerHyphen
import slatekit.common.lowerUnderscore
import slatekit.sampleapp.core.apis.SampleExtendedApi
import slatekit.sampleapp.core.apis.SamplePOKOApi


class Api_Naming_Tests : ApiTestsBase() {


    @Test fun can_use_naming_convention_lowerHyphen() {
        val apis = ApiContainer(ctx, apis = listOf(ApiReg(SamplePOKOApi::class)), auth = null, allowIO = false, namer = Namer("lower-hypen", ::lowerHyphen))
        assert( apis.getApi(""   , "sample-poko", "get-time"    ).success)
        assert( !apis.getApi(""  , "SamplePOKO" , "getTime"    ).success)
        assert( apis.getApi(""   , "sample-poko", "get-counter" ).success)
        assert( apis.getApi(""   , "sample-poko", "hello"      ).success)
        assert( apis.getApi(""   , "sample-poko", "request"    ).success)
        assert( apis.getApi(""   , "sample-poko", "response"   ).success)
        assert(!apis.getApi(""   , "sample-poko", "get-email"   ).success)
        assert(!apis.getApi(""   , "sample-poko", "get-ssn"     ).success)

        val result = apis.call("", "sample-poko", "get-counter", "", mapOf(), mapOf())
        assert(result.success)
        assert(result.value == 1)
    }


    @Test fun can_use_naming_convention_lowerUnderscore() {
        val apis = ApiContainer(ctx, apis = listOf(ApiReg(SampleExtendedApi::class, declaredOnly = false)),
                auth = null, allowIO = false, namer = Namer("lower-underscore", ::lowerUnderscore))
        assert( apis.getApi(""   , "sample_extended", "get_seconds" ).success)
        assert( apis.getApi(""   , "sample_extended", "get_time"    ).success)
        assert( apis.getApi(""   , "sample_extended", "get_counter" ).success)
        assert( apis.getApi(""   , "sample_extended", "hello"       ).success)
        assert( apis.getApi(""   , "sample_extended", "request"     ).success)
        assert( apis.getApi(""   , "sample_extended", "response"    ).success)
        assert(!apis.getApi(""   , "sample_extended", "get_email"   ).success)
        assert(!apis.getApi(""   , "sample_extended", "get_ssn"     ).success)

        val result = apis.call("", "sample_extended", "get_seconds", "", mapOf(), mapOf())
        assert(result.success)
        assert(result.value in 0..59)
    }
}