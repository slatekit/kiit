package test.jobs

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.threeten.bp.ZoneId

import slatekit.apis.ApiServer
import slatekit.apis.Verb
import slatekit.apis.routes.Api
import slatekit.apis.core.Reqs
import slatekit.apis.SetupType
import slatekit.common.*
import slatekit.core.queues.InMemoryQueue
import slatekit.context.AppContext
import slatekit.requests.InputArgs
import slatekit.common.Source
import slatekit.requests.CommonRequest
import slatekit.core.queues.AsyncQueue
import slatekit.core.queues.WrappedAsyncQueue
import slatekit.connectors.jobs.JobAPIWorker
import slatekit.connectors.jobs.JobQueue
import slatekit.jobs.*
import test.jobs.samples.SampleWorkerAPI
import test.setup.SampleTypes2Api
import test.setup.TestSupport

class Worker_Api_Tests : TestSupport {

    fun buildContainer(): ApiServer {
        val ctx = AppContext.simple(app,"queues")
        val api = SampleTypes2Api()
        val apis = ApiServer(ctx, apis = listOf(Api(api, area = "samples", name = "types2")))
        return apis
    }


    @Test
    fun can_send_to_queue(){
        runBlocking {
            // 1. context
            val ctx = AppContext.simple(app,"queues")

            // 2. queues
            val queues = listOf(AsyncQueue.of(InMemoryQueue.stringQueue()))

            // 3. apis
            val api = SampleWorkerAPI(ctx, queues)

            // 4. container
            val apis = ApiServer(ctx, apis = listOf(Api(api, setup = SetupType.Annotated)))

            // 5. send method call to queue
            val result = runBlocking {
                apis.executeAttempt("samples", "workerqueue", "test1", Verb.Post, mapOf(), mapOf(
                        "s" to "user1@abc.com",
                        "b" to true,
                        "i" to 123,
                        "d" to DateTimes.of(2018, 1, 27, 14, 30, 45).toString()
                ))
            }

            // 6. Ensure item is in queue
            Assert.assertEquals(1, queues[0].count())
            val entry = queues[0].next()
            Assert.assertNotNull(entry)
            entry?.let {
                Assert.assertNotNull(it.getTag("id"))
                Assert.assertNotNull(it.getTag("name"))
                Assert.assertEquals("api-queue", it.getTag("xid"))
            }
        }
    }


    @Test
    fun can_run_from_queue() {
        val container = buildContainer()
        val queues = listOf(InMemoryQueue.stringQueue())
        val worker = JobAPIWorker(container, Identity.test("api-worker"))
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
        val json1 = Reqs.toJsonString(sampleRequest)
        val queue = queues.first()
        queue.send(json1, mapOf("id" to "123", "refId" to "abc", "task" to "samples.types2.loadBasicTypes"))
        val entry = queue.next()!!

        val queueInfo = JobQueue("tests", Priority.Mid, WrappedAsyncQueue(queue))
        val job = JobQueue.task(Identity.test("samples"), entry, queueInfo)
        val result = runBlocking {
            worker.work(job)
        }
        //Assert.assertTrue( result.success )
        //Assert.assertTrue( result.getOrElse { null } == "user1@abc.com, true, 123, 2018-01-27T09:30:45Z" )
    }
}
