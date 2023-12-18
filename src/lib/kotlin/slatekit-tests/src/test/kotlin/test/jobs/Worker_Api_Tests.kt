package test.jobs

import kiit.apis.*
import kiit.apis.Middleware
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.threeten.bp.ZoneId

import kiit.apis.routes.Api
import kiit.apis.core.Reqs
import kiit.apis.setup.GlobalVersion
import kiit.apis.setup.api
import kiit.apis.setup.routes
import kiit.common.*
import kiit.core.queues.InMemoryQueue
import kiit.context.AppContext
import kiit.requests.InputArgs
import kiit.common.Source
import kiit.requests.CommonRequest
import kiit.core.queues.AsyncQueue
import kiit.core.queues.WrappedAsyncQueue
import kiit.connectors.jobs.JobAPIWorker
import kiit.connectors.jobs.JobQueue
import kiit.integration.common.ApiQueueSupport
import kiit.jobs.*
import kiit.results.Outcome
import test.apis.samples.Sample_API_1_Core
import test.jobs.samples.SampleWorkerAPI
import test.setup.SampleTypes2Api
import test.setup.TestSupport

class Worker_Api_Tests : TestSupport {

    fun buildContainer(): ApiServer {
        val ctx = AppContext.simple(app,"queues")
        val api = SampleTypes2Api()
        val routes = routes(versions = listOf(GlobalVersion("0", listOf(api(SampleTypes2Api::class, api)))))
        val apis = ApiServer(ctx, routes = routes)
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
            val api = SampleWorkerAPI(ctx)

            // 4. Policy( Middleware )
            val policy = TestQueueingMiddleware(queues)
            val policies = listOf("queued" to policy)

            // 4. container
            val routes = routes(versions = listOf(GlobalVersion("0", listOf(api(SampleWorkerAPI::class, api)))))
            val apis = ApiServer(ctx, routes = routes, middleware = policies)

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
            val count = queues[0].count()
            Assert.assertEquals(1, count)
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
        val worker = JobAPIWorker(container, Identity.test("kiit", "api-worker"))
        val sampleDate = DateTime.of(2018, 1, 27, 9, 30, 45, 0, ZoneId.of("UTC"))
        val sampleRequest = CommonRequest(
                path = "samples.types2.loadBasicTypes",
                parts = listOf("samples", "types2", "loadBasicTypes"),
                source = Source.Queue,
                verb = Verbs.POST,
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
        val job = JobQueue.task(Identity.test("kiit", "samples"), entry, queueInfo)
        val result = runBlocking {
            worker.work(job)
        }
        //Assert.assertTrue( result.success )
        //Assert.assertTrue( result.getOrElse { null } == "user1@abc.com, true, 123, 2018-01-27T09:30:45Z" )
    }
}



class TestQueueingMiddleware(val queues:List<AsyncQueue<String>> = listOf()) : ApiQueueSupport {
    override fun queues(): List<AsyncQueue<String>> = queues
}