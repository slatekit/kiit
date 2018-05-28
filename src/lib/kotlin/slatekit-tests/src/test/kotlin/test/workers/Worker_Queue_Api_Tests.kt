package test.workers

import org.junit.Test

import slatekit.apis.ApiContainer
import slatekit.apis.core.Annotated
import slatekit.apis.core.Api
import slatekit.common.*
import slatekit.common.queues.QueueSourceDefault
import slatekit.core.common.AppContext
import slatekit.core.workers.*
import slatekit.integration.workers.WorkerWithQueuesApi
import test.setup.SampleTypes2Api
import test.setup.WorkerSampleApi

class Worker_Queue_Api_Tests {

    fun buildContainer(): ApiContainer {
        val ctx = AppContext.simple("queues")
        val api = SampleTypes2Api()
        val apis = ApiContainer(ctx, apis = listOf(Api(api, area = "samples", name = "types2")), auth = null, allowIO = false)
        return apis
    }


    @Test
    fun can_send_to_queue(){
        // 1. context
        val ctx = AppContext.simple("queues")

        // 2. queues
        val queues = listOf(QueueSourceDefault())

        // 3. apis
        val api = WorkerSampleApi(ctx, queues)

        // 4. container
        val apis = ApiContainer(ctx, apis = listOf(Api(api, setup = Annotated)), auth = null, allowIO = false )

        // 5. worker system
        val sys = System()
        sys.register(WorkerWithQueuesApi(apis, queues, null, null, WorkerSettings()))

        // 6. send method call to queue
        val result = apis.call("samples", "workerqueue", "test1", "post", mapOf(), mapOf(
            "s" to "user1@abc.com",
            "b" to true,
            "i" to 123,
            "d" to DateTime.of(2018, 1, 27, 14, 30, 45)
        ))

        // 7. run worker
        assert(api._lastResult == "")
        sys.get(sys.defaultGroup)?.get(0)?.work()
        assert(api._lastResult == "user1@abc.com, true, 123")
    }


    @Test
    fun can_run_from_queue() {
        val container = buildContainer()
        val queues = listOf(QueueSourceDefault())
        val worker = WorkerWithQueuesApi(container, queues,null, null, WorkerSettings())
        val json1 = """
        {
             "version"  : "1.0",
             "path"     : "samples.types2.loadBasicTypes"
             "tag"      : "abcd",
             "meta"     : {
                 "api-key" : "2DFAD90A0F624D55B9F95A4648D7619A",
                 "token"   : "mmxZr5tkfMUV5/duU2rhHg"
             },
             "data"      : {
                 "s" : "user1@abc.com",
                 "b" : true,
                 "i" :123,
                 "d" : 20180127093045,
             }
        }
        """
        val queue = queues.first()
        queue.send(json1)
        worker.processItem(queue, queue.next())
        val result = worker.lastResult
        assert( result.success )
        assert( result.msg == "samples.types2.loadBasicTypes")
        assert( (result.getOrElse { null } as ResultEx<Any>).getOrElse { "" } == "user1@abc.com, true, 123, 2018-01-27T09:30:45-05:00[America/New_York]" )
    }
}
