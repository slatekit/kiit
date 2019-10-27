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

    @Test fun can_handle_error_at_api_level() {
        val number = "abc"
        ensure(
                protocol = Protocol.CLI,
                apis     = listOf(Api(SampleErrorsApi(), "app", "sampleErrors", auth = AuthMode.Token, roles = listOf(Roles.all), declaredOnly = false)),
                user     = Credentials(name = "kishore", roles = "dev"),
                request  = CommonRequest.path("app.sampleErrors.parseNumberWithExceptions", "get", mapOf(), mapOf(
                        "text" to number
                )),
                response = Outcomes.unexpected<Any>(Exception("unexpected error in api")).toResponse()
        )
    }


//    @Test fun can_handle_error_at_global_middleware_level() {
//        val number = "abc"
//        val errors = object: slatekit.apis.hooks.Errors(LoggerConsole()) {
//            override fun onError(ctx: Context, req: Request, target:Any, source: Any, ex: Exception?, args: Map<String, Any>?): Try<Any> {
//                val msg = "global middleware error handler"
//                return Failure(Exception(msg), Codes.UNEXPECTED)
//            }
//        }
//        ensure(
//                middleware = listOf(errors),
//                protocol = Protocol.CLI,
//                apis     = listOf(Api(SampleErrorsNoMiddlewareApi(), "app", "sampleErrors", auth = AuthMode.Token, roles = listOf(Roles.all), declaredOnly = false)),
//                user     = Credentials(name = "kishore", roles = "dev"),
//                request  = CommonRequest.path("app.sampleErrors.parseNumberWithExceptions", Verb.Read.name, mapOf(), mapOf(
//                        "text" to number
//                )),
//                response = Outcomes.unexpected<Any>(Exception("global middleware error handler")).toResponse()
//        )
//    }


    @Test fun can_handle_error_at_container_level() {
        val number = "abc"
        ensure(
                protocol = Protocol.CLI,
                apis     = listOf(Api(SampleErrorsNoMiddlewareApi(), "app", "sampleErrors", auth = AuthMode.Token, roles = listOf(Roles.all), declaredOnly = false)),
                user     = Credentials(name = "kishore", roles = "dev"),
                request  = CommonRequest.path("app.sampleErrors.parseNumberWithExceptions", Verb.Read.name, mapOf(), mapOf(
                        "text" to number
                )),
                response = Outcomes.unexpected<Any>(Exception("error executing : app.sampleErrors.parseNumberWithExceptions, check inputs")).toResponse()
        )
    }


    @Test fun can_handle_hooks() {
        val api = SampleMiddlewareApi()
        val apis = ApiHost(ctx, apis = listOf(Api(api, "app", "SampleMiddleware")), allowIO = false)
        val r1 = runBlocking { apis.call("app", "SampleMiddleware", "hello", Verb.Read, mapOf(), mapOf()) }
        val r2 = runBlocking { apis.call("app", "SampleMiddleware", "hello", Verb.Read, mapOf(), mapOf()) }

        Assert.assertTrue(api.onBeforeHookCount.size == 2)
        Assert.assertTrue(api.onAfterHookCount.size == 2)
        Assert.assertTrue(api.onBeforeHookCount[0].path == "app.SampleMiddleware.hello")
        Assert.assertTrue(api.onBeforeHookCount[1].path == "app.SampleMiddleware.hello")
        Assert.assertTrue(api.onAfterHookCount[0].path  == "app.SampleMiddleware.hello")
        Assert.assertTrue(api.onAfterHookCount[1].path  == "app.SampleMiddleware.hello")
    }


    @Test fun can_handle_filters_request_filtered_out() {
        val api = SampleMiddlewareApi()
        val apis = ApiHost(ctx, apis = listOf(Api(api, "app", "SampleMiddleware")), allowIO = false)
        val r1 = runBlocking { apis.call("app", "SampleMiddleware", "hi", Verb.Read, mapOf(), mapOf()) }

        Assert.assertTrue(!r1.success)
        Assert.assertTrue(r1.code == Codes.IGNORED.code)
        Assert.assertTrue(r1.msg == "Ignored")
    }


    @Test fun can_handle_filters_request_ok() {
        val api = SampleMiddlewareApi()
        val apis = ApiHost(ctx, apis = listOf(Api(api, "app", "SampleMiddleware")), allowIO = false)
        val r1 = runBlocking { apis.call("app", "SampleMiddleware", "hello", Verb.Read, mapOf(), mapOf()) }

        Assert.assertTrue(r1.success)
        Assert.assertTrue(r1.code == Codes.SUCCESS.code)
        Assert.assertTrue(r1.getOrElse { ""} == "hello world")
    }
}
