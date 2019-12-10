/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
package test.apis

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.threeten.bp.ZoneId
import slatekit.apis.*
import slatekit.apis.core.Api
import slatekit.apis.Setup
import slatekit.common.DateTimes
import slatekit.results.getOrElse
import test.apis.samples.Sample_API_1_Core
import test.apis.samples.Sample_API_1_Validation
import test.setup.StatusEnum

/**
 * Created by kishorereddy on 6/12/17.
 */

class Api_Validation_Tests : ApiTestsBase() {

    val AREA = "samples"
    val NAME = "core"
    val zone = ZoneId.of("EST")


    @Test
    fun can_fail_with_no_args() {
        val api = Sample_API_1_Validation()
        val apis = ApiServer(ctx, apis = listOf(Api(api, setup = Setup.Annotated)) )
        val r1 = runBlocking {
            apis.call("samples", "validation", "processInputs", Verb.Post, mapOf(), mapOf())
        }
        Assert.assertFalse(r1.success)
        r1.onFailure {

        }
        Assert.assertTrue(r1.getOrElse { "" } == "${StatusEnum.Active.name}:${StatusEnum.Active.value}")
    }


    @Test
    fun can_fail_with_missing_arg() {
        val api = Sample_API_1_Validation()
        val apis = ApiServer(ctx, apis = listOf(Api(api, setup = Setup.Annotated)) )
        val r1 = runBlocking {
            apis.call("samples", "validation", "processInputs", Verb.Post, mapOf(), mapOf(
                    Pair("code" , 2   ),
                    Pair("isOn" , true),
                    Pair("date" , DateTimes.of(2019, 10, 30, 8, 30, 45, 0, zone).toString())))
        }
        Assert.assertTrue(r1.success)
        Assert.assertTrue(r1.getOrElse { "" } == "${StatusEnum.Active.name}:${StatusEnum.Active.value}")
    }


    @Test
    fun can_fail_with_invalid_type() {
        val api = Sample_API_1_Validation()
        val apis = ApiServer(ctx, apis = listOf(Api(api, setup = Setup.Annotated)) )
        val r1 = runBlocking {
            apis.call("samples", "validation", "processInputs", Verb.Post, mapOf(), mapOf(
                    Pair("phone", "p1"  ),
                    Pair("code" , "abc" ),
                    Pair("isOn" , true),
                    Pair("date" , DateTimes.of(2019, 10, 30, 8, 30, 45, 0, zone).toString())))
        }
        Assert.assertTrue(r1.success)
        Assert.assertTrue(r1.getOrElse { "" } == "${StatusEnum.Active.name}:${StatusEnum.Active.value}")
    }


    @Test
    fun can_fail_with_conversion() {
        val api = Sample_API_1_Validation()
        val apis = ApiServer(ctx, apis = listOf(Api(api, setup = Setup.Annotated)) )
        val r1 = runBlocking {
            apis.call("samples", "validation", "processInputs", Verb.Post, mapOf(), mapOf(
                    Pair("phone", "p1"),
                    Pair("code" , 2.5 ),
                    Pair("isOn" , true),
                    Pair("date" , DateTimes.of(2019, 10, 30, 8, 30, 45, 0, zone).toString())))
        }
        Assert.assertTrue(r1.success)
        Assert.assertTrue(r1.getOrElse { "" } == "${StatusEnum.Active.name}:${StatusEnum.Active.value}")
    }
}
