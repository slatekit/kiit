package test.jobs

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.threeten.bp.ZoneId

import slatekit.apis.ApiHost
import slatekit.apis.setup.Annotated
import slatekit.apis.core.Api
import slatekit.apis.core.Requests
import slatekit.common.*
import slatekit.common.queues.QueueSourceInMemory
import slatekit.common.CommonContext
import slatekit.common.requests.InputArgs
import slatekit.common.requests.Source
import slatekit.integration.jobs.APIWorker
import slatekit.jobs.*
import test.setup.SampleTypes2Api

class Worker_Api_Tests {

    fun buildContainer(): ApiHost {
        val ctx = CommonContext.simple("queues")
        val api = SampleTypes2Api()
        val apis = ApiHost(ctx, apis = listOf(Api(api, area = "samples", name = "types2")), auth = null, allowIO = false)
        return apis
    }


    @Test
    fun can_send_to_queue(){
        // 1. context
        val ctx = CommonContext.simple("queues")

        // 2. queues
        val queues = listOf(QueueSourceInMemory.stringQueue())

        // 3. apis
        val api = SampleWorkerAPI(ctx, queues)

        // 4. container
        val apis = ApiHost(ctx, apis = listOf(Api(api, setup = Annotated)), auth = null, allowIO = false )

        // 5. send method call to queue
        val result = apis.call("samples", "workerqueue", "test1", "post", mapOf(), mapOf(
            "s" to "user1@abc.com",
            "b" to true,
            "i" to 123,
            "d" to DateTimes.of(2018, 1, 27, 14, 30, 45)
        ))

        // 6. Ensure item is in queue
        Assert.assertEquals(1, queues[0].count())
        val entry = queues[0].next()
        Assert.assertNotNull(entry)
        entry?.let {
            Assert.assertNotNull(it.getTag("id"))
            Assert.assertNotNull(it.getTag("refId"))
            Assert.assertEquals("api-queue", it.getTag("task"))
        }
    }


    @Test
    fun can_run_from_queue() {
        val container = buildContainer()
        val queues = listOf(QueueSourceInMemory.stringQueue())
        val worker = APIWorker(container, WorkerSettings(), Identity.test("api-worker"))
        val sampleDate = DateTime.of(2018, 1, 27, 9, 30, 45, 0, ZoneId.of("UTC"))
        val sampleRequest = CommonRequest(
                path = "samples.types2.loadBasicTypes",
                parts = listOf("samples", "types2", "loadBasicTypes"),
                source = Source.Queue,
                verb = "queue",
                data = InputArgs(mapOf(
                        "s" to "user1@abc.com",
                        "b" to true,
                        "i" to 123,
                        "d" to sampleDate
                )),
                meta = InputArgs(mapOf(
                        "api-key" to "2DFAD90A0F624D55B9F95A4648D7619A",
                        "token" to "mmxZr5tkfMUV5/duU2rhHg"
                )),
                raw = null,
                output = null,
                tag = "tag123",
                version = "1.0",
                timestamp = sampleDate
        )
        val json1 = Requests.toJsonString(sampleRequest)
        val queue = queues.first()
        queue.send(json1, mapOf("id" to "123", "refId" to "abc", "task" to "samples.types2.loadBasicTypes"))
        val entry = queue.next()!!

        val queueInfo = Queue("tests", Priority.Mid, queue)
        val job = Task(Identity.test("samples"), entry, queueInfo)
        val result = runBlocking {
            worker.work(job)
        }
        //Assert.assertTrue( result.success )
        //Assert.assertTrue( result.getOrElse { null } == "user1@abc.com, true, 123, 2018-01-27T09:30:45Z" )
    }
}
