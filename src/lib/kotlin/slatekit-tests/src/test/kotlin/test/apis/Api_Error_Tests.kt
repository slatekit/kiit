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
//import slatekit.integration.errors.ErrorHandler
//import slatekit.integration.errors.ErrorItem
//import slatekit.integration.errors.ErrorItemQueue
//import slatekit.integration.errors.ErrorItemService


class Api_Error_Tests : ApiTestsBase() {

    @Test fun can_handle_error_at_api_level() {

        // Register the error item
//        ctx.ent.prototype<Long, ErrorItem>(
//                ErrorItem::class,
//                Long::class,
//                LongIdGenerator(),
//                serviceType = ErrorItemService::class,
//                serviceCtx = ctx)
//
//        // get error components
//        val queue = ErrorItemQueue("errors", ctx)
//        val svc = queue.svc as ErrorItemService
//        val handler = ErrorHandler(ctx, queue, false)
//
//        // Api
//        val api = SampleRetryApi(handler)
//        val apis = ApiServer(ctx, apis = listOf(Api(api, setup = Setup.Annotated)), auth = null, allowIO = false )
//        svc.setApiHost(apis)
//        val t1 = apis.call("app", "retry", "test", "get", mapOf("token" to "abc"), mapOf("text" to "123"))
//        Assert.assertFalse(t1.success)
//        Assert.assertEquals(1, svc.count())
//
//        // Retry
//        val t2 = svc.retryLast(true)
//        Assert.assertTrue(t2.success)
//        Assert.assertEquals(0, svc.count())
    }
}