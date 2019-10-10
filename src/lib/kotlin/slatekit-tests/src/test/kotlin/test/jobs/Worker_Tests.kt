package test.jobs

import org.junit.Assert
import org.junit.Test
import slatekit.common.Status
import slatekit.common.ids.SimpleIdentity
import slatekit.jobs.WorkState
import slatekit.jobs.Worker
import slatekit.jobs.WorkRunner


class Worker_Tests {

    @Test
    fun can_setup() {
        val worker = OneTimeWorker(0, 3)
        val context =
        // Id
        Assert.assertEquals(worker.id.area , "samples")
        Assert.assertEquals(worker.id.env  , "dev")
        Assert.assertEquals(worker.id.agent, "tests")
        Assert.assertEquals(worker.id.name , "samples.dev.tests")

        // Status
        Assert.assertEquals(worker.status(), Status.InActive)

        // State
        Assert.assertEquals(worker.stats.counts.totalProcessed(), 0)
        Assert.assertEquals(worker.stats.counts.totalSucceeded(), 0)
        Assert.assertEquals(worker.stats.counts.totalInvalid(), 0)
        Assert.assertEquals(worker.stats.counts.totalIgnored(), 0)
        Assert.assertEquals(worker.stats.counts.totalDenied(), 0)
        Assert.assertEquals(worker.stats.counts.totalErrored(), 0)
        Assert.assertEquals(worker.stats.counts.totalUnexpected(), 0)

        // Info
        val info = worker.info()
        Assert.assertEquals(info[0], "id.name" to "samples.dev.tests")
        Assert.assertEquals(info[1], "app.attemptStart" to "0")
        Assert.assertEquals(info[2], "app.end" to "3")
    }


    @Test
    fun can_run() {
        val worker = OneTimeWorker(0, 3)
        val result = WorkRunner.run(worker)
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
    fun can_start() {
        val worker = OneTimeWorker(0, 3)
        val result = WorkRunner.attemptStart(worker)
        val flows = worker.currentFlows()
        Assert.assertTrue(result.success)
        Assert.assertEquals(worker.currentValue(), 4)
        Assert.assertEquals(worker.status(), Status.Complete)
        Assert.assertEquals(flows.size, 3)
        Assert.assertEquals(flows[0], "init")
        Assert.assertEquals(flows[1], "work")
        Assert.assertEquals(flows[2], "done")
        result.map {
            Assert.assertEquals(it, WorkState.Done)
        }
    }


    @Test
    fun can_start_paged() {
        val worker = PagedWorker(0, 3, 3)
        val result1 = WorkRunner.attemptStart(worker)
        Assert.assertTrue(result1.success)
        Assert.assertEquals(worker.currentValue(), 3)
        Assert.assertEquals(worker.status(), Status.Running)
        result1.map { Assert.assertEquals(it, WorkState.More) }

        // Work more
        val result2 =WorkRunner.work(worker)
        Assert.assertTrue(result2.success)
        Assert.assertEquals(worker.currentValue(), 6)
        Assert.assertEquals(worker.status(), Status.Running)
        result2.map { Assert.assertEquals(it, WorkState.More) }

        // Work last time ( limit 9 )
        val result3 =WorkRunner.work(worker)
        Assert.assertTrue(result3.success)
        Assert.assertEquals(worker.currentValue(), 9)
        Assert.assertEquals(worker.status(), Status.Complete)
        result3.map { Assert.assertEquals(it, WorkState.Done) }
    }


    @Test
    fun can_fail() {
        val worker = Worker<Int>(SimpleIdentity("samples", "dev", "tests"), operation = { task -> throw Exception("test fail") })
        val result = WorkRunner.run(worker)
        Assert.assertFalse(result.success)
        Assert.assertEquals(worker.status(), Status.Failed)
        result.onFailure {
            Assert.assertEquals(it.message, "test fail")
        }
    }
}