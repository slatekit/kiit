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
import kiit.apis.*
import kiit.apis.setup.api
import kiit.apis.setup.routes
import kiit.results.Codes
import kiit.results.Outcome
import test.setup.SampleMiddlewareApi

/**
 * Created by kishorereddy on 6/12/17.
 */


class Api_Middleware_Tests : ApiTestsBase() {

    @Test
    fun can_handle_hooks() {
        val apiMiddleware = TestApiMiddleware()
        val actionMiddleware = TestActionMiddleware()
        val api = SampleMiddlewareApi()
        val policies = listOf(
            "api_test" to apiMiddleware,
            "action_test" to actionMiddleware
        )
        val routes = routes(listOf(api(SampleMiddlewareApi::class, api)))
        val apis = ApiServer(ctx, routes = routes, middleware = policies )
        val r1 = runBlocking { apis.executeAttempt("app", "SampleMiddleware", SampleMiddlewareApi::hi.name, Verb.Post, mapOf(), mapOf()) }
        val r2 = runBlocking { apis.executeAttempt("app", "SampleMiddleware", SampleMiddlewareApi::hello.name, Verb.Post, mapOf(), mapOf()) }

        Assert.assertTrue(apiMiddleware.middlewareHook.size == 2)
        Assert.assertTrue(apiMiddleware.middlewareHook[0].request.path == "app.SampleMiddleware.hi")
        Assert.assertTrue(apiMiddleware.middlewareHook[1].request.path == "app.SampleMiddleware.hello")

        Assert.assertTrue(actionMiddleware.middlewareHook.size == 2)
        Assert.assertTrue(actionMiddleware.middlewareHook[0].request.path == "app.SampleMiddleware.hi")
        Assert.assertTrue(actionMiddleware.middlewareHook[1].request.path == "app.SampleMiddleware.hello")
        Assert.assertEquals("hi world", r1.getOrNull())
        Assert.assertEquals("hello world", r2.getOrNull())
    }
}


class TestApiMiddleware : Middleware{
    // Used for demo/testing purposes
    var middlewareHook = mutableListOf<ApiRequest>()

    override suspend fun process(req: ApiRequest, next: suspend (ApiRequest) -> Outcome<ApiResult>): Outcome<ApiResult> {
        middlewareHook.add(req)
        return next(req)
    }
}


class TestActionMiddleware : Middleware{
    // Used for demo/testing purposes
    var middlewareHook = mutableListOf<ApiRequest>()

    override suspend fun process(req: ApiRequest, next: suspend (ApiRequest) -> Outcome<ApiResult>): Outcome<ApiResult> {
        middlewareHook.add(req)
        return next(req)
    }
}