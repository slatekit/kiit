/**
 <kiit_header>
url: www.kiit.dev
git: www.github.com/slatekit/kiit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.

 </kiit_header>
 */
package test.apis

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.threeten.bp.ZoneId
import kiit.apis.*
import kiit.apis.routes.Api
import kiit.apis.SetupType
import kiit.apis.setup.GlobalVersion
import kiit.apis.setup.api
import kiit.apis.setup.routes
import kiit.common.DateTimes
import kiit.results.*
import test.apis.samples.Sample_API_1_Core
import test.apis.samples.Sample_API_1_Validation

/**
 * Created by kishorereddy on 6/12/17.
 */

class Api_Validation_Tests : ApiTestsBase() {

    val AREA = "samples"
    val NAME = "core"
    val zone = ZoneId.of("EST")


    @Test
    fun can_fail_with_missing_args() {
        val routes = routes(versions = listOf(GlobalVersion("0", listOf(api(Sample_API_1_Validation::class, Sample_API_1_Validation())))))
        val apis = ApiServer(ctx, routes = routes)
        val r1 = runBlocking {
            apis.executeAttempt("tests", "validation", Sample_API_1_Validation::processInputs.name, Verb.Post, mapOf(), mapOf())
        }
        Assert.assertFalse(r1.success)
        r1.onFailure {
            val ex = it as ExceptionErr
            val err = ex.err as Err.ErrorList
            Assert.assertEquals(4, err.errors.size)
            Assert.assertEquals("phone", (err.errors[0] as Err.ErrorField).field)
            Assert.assertEquals("code", (err.errors[1]  as Err.ErrorField).field)
            Assert.assertEquals("isOn", (err.errors[2]  as Err.ErrorField).field)
            Assert.assertEquals("date", (err.errors[3]  as Err.ErrorField).field)
            println("done")
        }
    }


    @Test
    fun can_fail_with_conversion() {
        val routes = routes(versions = listOf(GlobalVersion("0", listOf(api(Sample_API_1_Validation::class, Sample_API_1_Validation())))))
        val apis = ApiServer(ctx, routes = routes)
        val r1 = runBlocking {
            apis.executeAttempt("tests", "validation", Sample_API_1_Validation::processInputs.name, Verb.Post, mapOf(), mapOf(
                    Pair("phone", "p1"),
                    Pair("code" , "abc" ),
                    Pair("isOn" , "something"),
                    Pair("date" , DateTimes.of(2019, 10, 30, 8, 30, 45, 0, zone).toString())))
        }
        Assert.assertFalse(r1.success)
        r1.onFailure {
            val ex = it as ExceptionErr
            val err = ex.err as Err.ErrorList
            Assert.assertEquals(1, err.errors.size)
            Assert.assertEquals("code", (err.errors[0] as Err.ErrorField).field)
            println("done")
        }
    }
}
