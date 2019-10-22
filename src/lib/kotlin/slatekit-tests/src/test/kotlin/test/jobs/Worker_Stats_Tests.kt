package test.jobs

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import slatekit.common.metrics.Recorder
import slatekit.jobs.support.Runner
import slatekit.jobs.WorkerContext


class Worker_Stats_Tests {

    @Test
    fun can_ensure_no_runs() {
        val worker = OneTimeWorker(0, 10)
        val context = WorkerContext(worker.id, worker, Recorder.of(worker.id))
        val runs = context.stats.calls
        Assert.assertEquals(runs.hasRun(), false)
        Assert.assertNull  (runs.lastTime())
        Assert.assertEquals(runs.totalRuns(), 0)
        Assert.assertEquals(runs.totalPassed(), 0)
        Assert.assertEquals(runs.totalFailed(), 0)
    }


    @Test
    fun can_record_single_success() {
        val worker = OneTimeWorker(0, 10)
        val context = WorkerContext(worker.id, worker, Recorder.of(worker.id))
        val runs = context.stats.calls
        runBlocking {
            Runner.record(context, { })
        }
        Assert.assertEquals (runs.hasRun(), true)
        Assert.assertNotNull(runs.lastTime())
        Assert.assertEquals (runs.totalRuns(), 1)
        Assert.assertEquals (runs.totalPassed(), 1)
        Assert.assertEquals (runs.totalFailed(), 0)
    }


    @Test
    fun can_record_single_failure() {
        val worker = OneTimeWorker(0, 10)
        val context = WorkerContext(worker.id, worker, Recorder.of(worker.id))
        val runs = context.stats.calls
        runBlocking {
            Runner.record(context, { throw Exception("testing") })
        }
        Assert.assertEquals (runs.hasRun(), true)
        Assert.assertNotNull(runs.lastTime())
        Assert.assertEquals (runs.totalRuns(), 1)
        Assert.assertEquals (runs.totalPassed(), 0)
        Assert.assertEquals (runs.totalFailed(), 1)
    }


    @Test
    fun can_ensure_multiple_runs() {
        val worker = OneTimeWorker(0, 10)
        val context = WorkerContext(worker.id, worker, Recorder.of(worker.id))
        val runs = context.stats.calls
        val count =  3L
        runBlocking {
            (0 until count).forEach {  Runner.record(context, { }) }
        }
        Assert.assertEquals (runs.hasRun(), true)
        Assert.assertNotNull(runs.lastTime())
        Assert.assertEquals(runs.totalRuns(), count)
        Assert.assertEquals(runs.totalPassed(), count)
        Assert.assertEquals(runs.totalFailed(), 0)
    }


    @Test
    fun can_ensure_multiple_success_failures() {
        val worker = OneTimeWorker(0, 10)
        val context = WorkerContext(worker.id, worker, Recorder.of(worker.id))
        val runs = context.stats.calls
        val successes =  3L
        val failures =  3L
        runBlocking {
            (0 until successes).forEach {  Runner.record(context, { }) }
            (0 until failures).forEach {  Runner.record(context, { throw Exception("testing") }) }
        }
        Assert.assertEquals (runs.hasRun(), true)
        Assert.assertNotNull(runs.lastTime())
        Assert.assertEquals (runs.totalRuns(), successes + failures)
        Assert.assertEquals (runs.totalPassed(), successes)
        Assert.assertEquals (runs.totalFailed(), failures)
    }
}