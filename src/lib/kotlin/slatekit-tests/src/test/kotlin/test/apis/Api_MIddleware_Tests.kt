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
import slatekit.apis.*
import slatekit.apis.core.Api
import slatekit.common.*
import slatekit.common.auth.Roles
import slatekit.common.info.Credentials
import slatekit.common.CommonRequest
import slatekit.results.Codes
import slatekit.results.builders.Outcomes
import slatekit.results.getOrElse
import test.setup.SampleErrorsApi
import test.setup.SampleErrorsNoMiddlewareApi
import test.setup.SampleMiddlewareApi

/**
 * Created by kishorereddy on 6/12/17.
 */


class Api_Middleware_Tests : ApiTestsBase() {

    @Test fun can_handle_hooks() {
        val api = SampleMiddlewareApi()
        val apis = ApiServer(ctx, apis = listOf(Api(api, "app", "SampleMiddleware")))
        val r1 = runBlocking { apis.call("app", "SampleMiddleware", "hello", Verb.Post, mapOf(), mapOf()) }
        val r2 = runBlocking { apis.call("app", "SampleMiddleware", "hello", Verb.Post, mapOf(), mapOf()) }

        Assert.assertTrue(api.onBeforeHookCount.size == 2)
        Assert.assertTrue(api.onAfterHookCount.size == 2)
        Assert.assertTrue(api.onBeforeHookCount[0].request.path == "app.SampleMiddleware.hello")
        Assert.assertTrue(api.onBeforeHookCount[1].request.path == "app.SampleMiddleware.hello")
        Assert.assertTrue(api.onAfterHookCount[0].request.path  == "app.SampleMiddleware.hello")
        Assert.assertTrue(api.onAfterHookCount[1].request.path  == "app.SampleMiddleware.hello")
    }


    @Test fun can_handle_filters_request_pass() {
        val api = SampleMiddlewareApi()
        val apis = ApiServer(ctx, apis = listOf(Api(api, "app", "SampleMiddleware")))
        val r1 = runBlocking { apis.call("app", "SampleMiddleware", "hello", Verb.Post, mapOf(), mapOf()) }

        Assert.assertTrue(r1.success)
        Assert.assertTrue(r1.code == Codes.SUCCESS.code)
        Assert.assertTrue(r1.getOrElse { ""} == "hello world")
    }


    @Test fun can_handle_filters_request_fail() {
        val api = SampleMiddlewareApi()
        val apis = ApiServer(ctx, apis = listOf(Api(api, "app", "SampleMiddleware")))
        val r1 = runBlocking { apis.call("app", "SampleMiddleware", "hi", Verb.Post, mapOf(), mapOf()) }

        Assert.assertTrue(!r1.success)
        Assert.assertTrue(r1.code == Codes.ERRORED.code)
        Assert.assertTrue(r1.msg == Codes.ERRORED.msg)
    }


    @Test fun can_handle_error_at_api_level() {
        val api = SampleMiddlewareApi()
        val apis = ApiServer(ctx, apis = listOf(Api(api, "app", "SampleMiddleware")))
        val r1 = runBlocking { apis.call("app", "SampleMiddleware", "hi", Verb.Post, mapOf(), mapOf()) }

        Assert.assertTrue(!r1.success)
        Assert.assertTrue(r1.code == Codes.ERRORED.code)
        Assert.assertTrue(r1.msg == Codes.ERRORED.msg)
        Assert.assertTrue(api.onErrorHookCount.size == 1)
    }
}
