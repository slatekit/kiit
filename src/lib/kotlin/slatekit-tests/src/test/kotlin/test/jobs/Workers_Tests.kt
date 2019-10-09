package test.jobs

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import slatekit.jobs.WorkRunner


class Workers_Tests {

    @Test
    fun can_ensure_no_runs() {
        val worker = OneTimeWorker(0, 10)
        Assert.assertEquals(worker.stats.hasRun, false)
        Assert.assertNull(worker.stats.lastRunTime.get())
        Assert.assertEquals(worker.stats.totalRuns.get(), 0)
        Assert.assertEquals(worker.stats.totalRunsPassed.get(), 0)
        Assert.assertEquals(worker.stats.totalRunsFailed.get(), 0)
    }


    @Test
    fun can_record_single_success() {
        val worker = OneTimeWorker(0, 10)
        runBlocking {
            WorkRunner.record(worker, { })
        }
        Assert.assertEquals(worker.stats.hasRun, true)
        Assert.assertNotNull(worker.stats.lastRunTime.get())
        Assert.assertEquals(worker.stats.totalRuns.get(), 1)
        Assert.assertEquals(worker.stats.totalRunsPassed.get(), 1)
        Assert.assertEquals(worker.stats.totalRunsFailed.get(), 0)
    }


    @Test
    fun can_record_single_failure() {
        val worker = OneTimeWorker(0, 10)
        runBlocking {
            WorkRunner.record(worker, { throw Exception("testing") })
        }
        Assert.assertEquals(worker.stats.hasRun, true)
        Assert.assertNotNull(worker.stats.lastRunTime.get())
        Assert.assertEquals(worker.stats.totalRuns.get(), 1)
        Assert.assertEquals(worker.stats.totalRunsPassed.get(), 0)
        Assert.assertEquals(worker.stats.totalRunsFailed.get(), 1)
    }


    @Test
    fun can_ensure_multiple_runs() {
        val worker = OneTimeWorker(0, 10)
        val count =  3L
        runBlocking {
            (0 until count).forEach {  WorkRunner.record(worker, { }) }
        }
        Assert.assertEquals(worker.stats.hasRun, true)
        Assert.assertNotNull(worker.stats.lastRunTime.get())
        Assert.assertEquals(worker.stats.totalRuns.get(), count)
        Assert.assertEquals(worker.stats.totalRunsPassed.get(), count)
        Assert.assertEquals(worker.stats.totalRunsFailed.get(), 0)
    }


    @Test
    fun can_ensure_multiple_success_failures() {
        val worker = OneTimeWorker(0, 10)
        val successes =  3L
        val failures =  3L
        runBlocking {
            (0 until successes).forEach {  WorkRunner.record(worker, { }) }
            (0 until failures).forEach {  WorkRunner.record(worker, { throw Exception("testing") }) }
        }
        Assert.assertEquals(worker.stats.hasRun, true)
        Assert.assertNotNull(worker.stats.lastRunTime.get())
        Assert.assertEquals(worker.stats.totalRuns.get(), successes + failures)
        Assert.assertEquals(worker.stats.totalRunsPassed.get(), successes)
        Assert.assertEquals(worker.stats.totalRunsFailed.get(), failures)
    }
}