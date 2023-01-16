/**
 <kiit_header>
url: www.slatekit.com
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
import slatekit.apis.*
import slatekit.apis.routes.Api
import kiit.results.Codes
import kiit.results.getOrElse
import test.setup.SampleMiddlewareApi

/**
 * Created by kishorereddy on 6/12/17.
 */


class Api_Middleware_Tests : ApiTestsBase() {

    @Test fun can_handle_hooks() {
        val api = SampleMiddlewareApi()
        val apis = ApiServer(ctx, apis = listOf(Api(api, "app", "SampleMiddleware")))
        val r1 = runBlocking { apis.executeAttempt("app", "SampleMiddleware", SampleMiddlewareApi::hello.name, Verb.Post, mapOf(), mapOf()) }
        val r2 = runBlocking { apis.executeAttempt("app", "SampleMiddleware", SampleMiddlewareApi::hello.name, Verb.Post, mapOf(), mapOf()) }

        Assert.assertTrue(api.middlewareHook.size == 2)
        Assert.assertTrue(api.middlewareHook[0].request.path == "app.SampleMiddleware.hello")
        Assert.assertTrue(api.middlewareHook[1].request.path == "app.SampleMiddleware.hello")
        Assert.assertEquals("hello world", r2.getOrNull())
    }
}
