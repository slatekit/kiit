package test.apis

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import kiit.apis.routes.Api
import kiit.apis.ApiServer
import kiit.apis.Settings
import kiit.apis.Verb
import kiit.apis.Verbs
import kiit.apis.setup.GlobalVersion
import kiit.apis.setup.api
import kiit.apis.setup.routes
import kiit.utils.naming.LowerHyphenNamer
import kiit.utils.naming.LowerUnderscoreNamer
import kiit.results.getOrElse
import test.apis.samples.Sample_API_1_Core
import test.setup.SampleExtendedApi
import test.setup.SamplePOKOApi


class Api_Naming_Tests : ApiTestsBase() {


    @Test fun can_use_naming_convention_lowerHyphen() {
        val namer = LowerHyphenNamer()
        val routes = routes(versions = listOf(GlobalVersion("0", listOf(api(SamplePOKOApi::class, SamplePOKOApi())))), namer = namer)
        val apis = ApiServer(ctx, routes, settings = Settings(naming = namer))
        Assert.assertNotNull( apis.get(Verbs.GET , "tests"   , "sample-poko", "get-time"    ))
        Assert.assertNull   ( apis.get(Verbs.GET , "tests"   , "SamplePOKO" , "getTime"     ))
        Assert.assertNotNull( apis.get(Verbs.GET , "tests"   , "sample-poko", "get-counter" ))
        Assert.assertNotNull( apis.get(Verbs.POST, "tests"   , "sample-poko", "hello"       ))
        Assert.assertNotNull( apis.get(Verbs.POST, "tests"   , "sample-poko", "request"     ))
        Assert.assertNotNull( apis.get(Verbs.POST, "tests"   , "sample-poko", "response"    ))
        Assert.assertNull   ( apis.get(Verbs.GET , "tests"   , "sample-poko", "get-email"   ))
        Assert.assertNull   ( apis.get(Verbs.GET , "tests"   , "sample-poko", "get-ssn"))

        val result = runBlocking {
            apis.executeAttempt("tests", "sample-poko", "get-counter", Verb.Get, mapOf(), mapOf())
        }
        Assert.assertTrue(result.success)
        Assert.assertTrue(result.getOrElse { 0 } == 1)
    }


    @Test fun can_use_naming_convention_lowerUnderscore() {
        val namer = LowerUnderscoreNamer()
        val routes = routes(versions = listOf(GlobalVersion("0", listOf(api(SampleExtendedApi::class, SampleExtendedApi(), declared = false)))), namer = namer)
        val apis = ApiServer(ctx, routes, settings = Settings(naming = namer))
        Assert.assertNotNull( apis.get(Verbs.GET , "tests"   , "sample_extended", "get_seconds" ))
        Assert.assertNotNull( apis.get(Verbs.GET , "tests"   , "sample_extended", "get_time"    ))
        Assert.assertNotNull( apis.get(Verbs.GET , "tests"   , "sample_extended", "get_counter" ))
        Assert.assertNotNull( apis.get(Verbs.POST, "tests"   , "sample_extended", "hello"       ))
        Assert.assertNotNull( apis.get(Verbs.POST, "tests"   , "sample_extended", "request"     ))
        Assert.assertNotNull( apis.get(Verbs.POST, "tests"   , "sample_extended", "response"    ))
        Assert.assertNull(    apis.get(Verbs.POST, "tests"   , "sample_extended", "get_email"   ))
        Assert.assertNull(    apis.get(Verbs.GET , "tests"   , "sample_extended", "get_ssn"     ))

        val result = runBlocking {
            apis.executeAttempt("tests", "sample_extended", "get_seconds", Verb.Get, mapOf(), mapOf())
        }
        Assert.assertTrue(result.success)
        Assert.assertTrue(result.getOrElse { 0 } in 0..59)
    }
}
