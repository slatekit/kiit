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

import org.junit.Test
import slatekit.apis.*
import slatekit.apis.core.Api
import slatekit.common.*
import slatekit.common.results.BAD_REQUEST
import slatekit.common.results.ResultFuncs
import slatekit.common.results.SUCCESS
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
                protocol = CliProtocol,
                apis     = listOf(Api(SampleErrorsApi(), "app", "sampleErrors", declaredOnly = false)),
                user     = Credentials(name = "kishore", roles = "dev"),
                request  = Request.path("app.sampleErrors.parseNumberWithExceptions", "get", mapOf(), mapOf(
                        "text" to number
                )),
                response = ResultFuncs.unexpectedError<Any>("unexpected error in api").toResponse()
        )
    }


    @Test fun can_handle_error_at_global_middleware_level() {
        val number = "abc"
        val errors = object: slatekit.apis.middleware.Error {
            override fun onError(ctx: Context, req: Request, target:Any, source: Any, ex: Exception?, args: Map<String, Any>?): Result<Any> {
                return ResultFuncs.unexpectedError("global middleware error handler", err = ex)
            }
        }
        ensure(
                middleware = listOf(errors),
                protocol = CliProtocol,
                apis     = listOf(Api(SampleErrorsNoMiddlewareApi(), "app", "sampleErrors", declaredOnly = false)),
                user     = Credentials(name = "kishore", roles = "dev"),
                request  = Request.path("app.sampleErrors.parseNumberWithExceptions", "get", mapOf(), mapOf(
                        "text" to number
                )),
                response = ResultFuncs.unexpectedError<Any>("global middleware error handler").toResponse()
        )
    }


    @Test fun can_handle_error_at_container_level() {
        val number = "abc"
        ensure(
                protocol = CliProtocol,
                apis     = listOf(Api(SampleErrorsNoMiddlewareApi(), "app", "sampleErrors", declaredOnly = false)),
                user     = Credentials(name = "kishore", roles = "dev"),
                request  = Request.path("app.sampleErrors.parseNumberWithExceptions", "get", mapOf(), mapOf(
                        "text" to number
                )),
                response = ResultFuncs.unexpectedError<Any>("error executing : app.sampleErrors.parseNumberWithExceptions, check inputs").toResponse()
        )
    }


    @Test fun can_handle_hooks() {
        val api = SampleMiddlewareApi()
        val apis = ApiContainer(ctx, apis = listOf(Api(api, "app", "SampleMiddleware")), allowIO = false)
        val r1 = apis.call("app", "SampleMiddleware", "hello", "get", mapOf(), mapOf())
        val r2 = apis.call("app", "SampleMiddleware", "hello", "get", mapOf(), mapOf())

        assert(api.onBeforeHookCount.size == 2)
        assert(api.onAfterHookCount.size == 2)
        assert(api.onBeforeHookCount[0].path == "app.SampleMiddleware.hello")
        assert(api.onBeforeHookCount[1].path == "app.SampleMiddleware.hello")
        assert(api.onAfterHookCount[0].path  == "app.SampleMiddleware.hello")
        assert(api.onAfterHookCount[1].path  == "app.SampleMiddleware.hello")
    }


    @Test fun can_handle_filters_request_filtered_out() {
        val api = SampleMiddlewareApi()
        val apis = ApiContainer(ctx, apis = listOf(Api(api, "app", "SampleMiddleware")), allowIO = false)
        val r1 = apis.call("app", "SampleMiddleware", "hi", "get", mapOf(), mapOf())

        assert(!r1.success)
        assert(r1.code == BAD_REQUEST)
        assert(r1.msg == "filtered out")
    }


    @Test fun can_handle_filters_request_ok() {
        val api = SampleMiddlewareApi()
        val apis = ApiContainer(ctx, apis = listOf(Api(api, "app", "SampleMiddleware")), allowIO = false)
        val r1 = apis.call("app", "SampleMiddleware", "hello", "get", mapOf(), mapOf())

        assert(r1.success)
        assert(r1.code == SUCCESS)
        assert(r1.value == "hello world")
    }
}