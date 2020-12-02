package test.jobs

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import slatekit.common.Status
import slatekit.common.Identity
import slatekit.jobs.workers.WorkResult
import slatekit.jobs.workers.Worker
import slatekit.jobs.support.Runner

class Worker_Tests {

    @Test
    fun can_setup() {
        val worker = OneTimeWorker(0, 3)
        val context =
        // Id
        Assert.assertEquals(worker.id.area , "tests")
        Assert.assertEquals(worker.id.service, "OneTimeWorker")
        Assert.assertEquals(worker.id.env  , "dev")
        Assert.assertEquals(worker.id.name , "tests.OneTimeWorker")

        // Status
        Assert.assertEquals(worker.status(), Status.InActive)

        // State
        Assert.assertEquals(worker.stats.counts.processed.get(), 0)
        Assert.assertEquals(worker.stats.counts.succeeded.get(), 0)
        Assert.assertEquals(worker.stats.counts.invalid.get(), 0)
        Assert.assertEquals(worker.stats.counts.ignored.get(), 0)
        Assert.assertEquals(worker.stats.counts.denied.get(), 0)
        Assert.assertEquals(worker.stats.counts.errored.get(), 0)
        Assert.assertEquals(worker.stats.counts.unknown.get(), 0)

        // Info
        val info = worker.info()
        Assert.assertEquals(info[0], "id.name" to "tests.OneTimeWorker")
        Assert.assertEquals(info[1], "app.attemptStart" to "0")
        Assert.assertEquals(info[2], "app.end" to "3")
    }


    @Test
    fun can_run() {
        val worker = OneTimeWorker(0, 3)
        val result = runBlocking { Runner.run(worker) }
        val flows = worker.currentFlows()

        Assert.assertTrue(result.success)
        Assert.assertEquals(worker.currentValue(), 4)
        Assert.assertEquals(worker.status(), Status.Complete)
        Assert.assertEquals(flows.size, 3)
        Assert.assertEquals(flows[0], "init")
        Assert.assertEquals(flows[1], "work")
        Assert.assertEquals(flows[2], "done")
        result.map {
            Assert.assertEquals(it, Status.Complete)
        }
    }


    @Test
    fun can_run_paged() {
        val worker = PagedWorker(0, 3, 3)
        val result1 = runBlocking { Runner.attemptStart(worker) }
        Assert.assertTrue(result1.success)
        Assert.assertEquals(worker.currentValue(), 3)
        Assert.assertEquals(worker.status(), Status.Running)
        result1.map { Assert.assertEquals(it, WorkResult.More) }

        // Work more
        val result2 = runBlocking { Runner.work(worker) }
        Assert.assertTrue(result2.success)
        Assert.assertEquals(worker.currentValue(), 6)
        Assert.assertEquals(worker.status(), Status.Running)
        result2.map { Assert.assertEquals(it, WorkResult.More) }

        // Work last time ( limit 9 )
        val result3 = runBlocking {  Runner.work(worker) }
        Assert.assertTrue(result3.success)
        Assert.assertEquals(worker.currentValue(), 9)
        Assert.assertEquals(worker.status(), Status.Complete)
        result3.map { Assert.assertEquals(it, WorkResult.Done) }
    }


    @Test
    fun can_start() {
        val worker = OneTimeWorker(0, 3)
        val result = runBlocking { Runner.attemptStart(worker) }
        val flows = worker.currentFlows()
        Assert.assertTrue(result.success)
        Assert.assertEquals(worker.currentValue(), 4)
        Assert.assertEquals(worker.status(), Status.Complete)
        Assert.assertEquals(flows.size, 3)
        Assert.assertEquals(flows[0], "init")
        Assert.assertEquals(flows[1], "work")
        Assert.assertEquals(flows[2], "done")
        result.map {
            Assert.assertEquals(it, WorkResult.Done)
        }
    }


    @Test
    fun can_fail() {
        val worker = Worker<Int>(Identity.test("worker1"), operation = { task -> throw Exception("test fail") })
        val result = runBlocking { Runner.run(worker) }
        Assert.assertFalse(result.success)
        Assert.assertEquals(worker.status(), Status.Failed)
        result.onFailure {
            Assert.assertEquals(it.message, "test fail")
        }
    }
}