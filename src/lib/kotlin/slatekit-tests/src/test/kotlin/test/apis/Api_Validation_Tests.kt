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
import slatekit.results.ErrorField
import slatekit.results.ErrorList
import slatekit.results.ExceptionErr
import slatekit.results.getOrElse
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
    fun can_fail_with_missing_args() {
        val api = Sample_API_1_Validation()
        val apis = ApiServer(ctx, apis = listOf(Api(api, setup = Setup.Annotated)) )
        val r1 = runBlocking {
            apis.call("samples", "validation", Sample_API_1_Validation::processInputs.name, Verb.Post, mapOf(), mapOf())
        }
        Assert.assertFalse(r1.success)
        r1.onFailure {
            val ex = it as ExceptionErr
            val err = ex.err as ErrorList
            Assert.assertEquals(4, err.errors.size)
            Assert.assertEquals("phone", (err.errors[0] as ErrorField).field)
            Assert.assertEquals("code", (err.errors[1] as ErrorField).field)
            Assert.assertEquals("isOn", (err.errors[2] as ErrorField).field)
            Assert.assertEquals("date", (err.errors[3] as ErrorField).field)
            println("done")
        }
    }


    @Test
    fun can_fail_with_conversion() {
        val api = Sample_API_1_Validation()
        val apis = ApiServer(ctx, apis = listOf(Api(api, setup = Setup.Annotated)) )
        val r1 = runBlocking {
            apis.call("samples", "validation", Sample_API_1_Validation::processInputs.name, Verb.Post, mapOf(), mapOf(
                    Pair("phone", "p1"),
                    Pair("code" , "abc" ),
                    Pair("isOn" , "something"),
                    Pair("date" , DateTimes.of(2019, 10, 30, 8, 30, 45, 0, zone).toString())))
        }
        Assert.assertFalse(r1.success)
        r1.onFailure {
            val ex = it as ExceptionErr
            val err = ex.err as ErrorList
            Assert.assertEquals(1, err.errors.size)
            Assert.assertEquals("code", (err.errors[0] as ErrorField).field)
            println("done")
        }
    }
}
