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
package test

import org.junit.Test
import slatekit.apis.*
import slatekit.apis.containers.ApiContainerCLI
import slatekit.common.results.BAD_REQUEST
import slatekit.common.results.SUCCESS
import slatekit.sampleapp.core.apis.SampleMiddlewareApi
import slatekit.test.common.MyAuthProvider
import slatekit.tests.common.UserApi

/**
 * Created by kishorereddy on 6/12/17.
 */


class Api_Middleware_Tests : ApiTestsBase() {


    // ===================================================================
    //describe( "API Container with middleware" ) {
    @Test fun middleware_with_hooks() {
        val api = SampleMiddlewareApi(true, false)
        val apis = ApiContainerCLI(ctx, apis = listOf(ApiReg(api)))
        val r1 = apis.call("", "SampleMiddleware", "hello", "get", mapOf(), mapOf())
        val r2 = apis.call("", "SampleMiddleware", "hello", "get", mapOf(), mapOf())

        assert(api.onBeforeHookCount.size == 2)
        assert(api.onAfterHookCount.size == 2)
        assert(api.onBeforeHookCount[0].path == "SampleMiddleware.hello")
        assert(api.onBeforeHookCount[1].path == "SampleMiddleware.hello")
        assert(api.onAfterHookCount[0].path  == "SampleMiddleware.hello")
        assert(api.onAfterHookCount[1].path  == "SampleMiddleware.hello")
    }


    @Test fun middleware_with_filters_request_filtered_out() {
        val api = SampleMiddlewareApi(true, true)
        val apis = ApiContainerCLI(ctx, apis = listOf(ApiReg(api)))
        val r1 = apis.call("", "SampleMiddleware", "hi", "get", mapOf(), mapOf())

        assert(!r1.success)
        assert(r1.code == BAD_REQUEST)
        assert(r1.msg == "filtered out")
    }


    @Test fun middleware_with_filters_request_ok() {
        val api = SampleMiddlewareApi(true, true)
        val apis = ApiContainerCLI(ctx, apis = listOf(ApiReg(api)))
        val r1 = apis.call("", "SampleMiddleware", "hello", "get", mapOf(), mapOf())

        assert(r1.success)
        assert(r1.code == SUCCESS)
        assert(r1.value == "hello world")
    }
}