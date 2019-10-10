package test.jobs

import kotlinx.coroutines.runBlocking
import org.junit.Test
import slatekit.common.log.LoggerConsole
import slatekit.common.log.Warn
import slatekit.jobs.JobAction
import slatekit.jobs.JobManager
import slatekit.jobs.JobRequest

class Manager_Tests {

    @Test
    fun can_start() {
        val worker = PagedWorker(0, 3, 3)
        val manager = JobManager(listOf(worker), MockScheduler(), LoggerConsole(Warn, "manager"))
        runBlocking {
            val req = JobRequest.WorkRequest(JobAction.Start, worker.id, 0, "")
            manager.manageWorker(req, false)
        }
        println("done")
    }


    @Test
    fun can_run_paged() {
        runBlocking {
            val worker = PagedWorker(0, 3, 3)
            val manager = JobManager(listOf(worker), MockScheduler(), LoggerConsole(Warn, "manager"))
            manager.request(JobAction.Start , worker.id, "test")
            println("done")
        }
    }


    @Test
    fun can_pause() {
        runBlocking {
            val worker = PagedWorker(0, 3, 3)
            val manager = JobManager(listOf(worker), MockScheduler(), LoggerConsole(Warn, "manager"))
            manager.request(JobAction.Start , worker.id, "test")
            manager.request(JobAction.Pause , worker.id, "test")
            println("done")
        }
    }


    @Test
    fun can_stop() {
        runBlocking {
            val worker = PagedWorker(0, 3, 3)
            val manager = JobManager(listOf(worker), MockScheduler(), LoggerConsole(Warn, "manager"))
            manager.request(JobAction.Start , worker.id, "test")
            manager.request(JobAction.Stop  , worker.id, "test")
            println("done")
        }
    }


    @Test
    fun can_resume_after_pause() {
        runBlocking {
            val worker = PagedWorker(0, 3, 3)
            val manager = JobManager(listOf(worker), MockScheduler(), LoggerConsole(Warn, "manager"))
            manager.request(JobAction.Start , worker.id, "test")
            manager.request(JobAction.Pause , worker.id, "test")
            manager.request(JobAction.Resume, worker.id, "test")
            println("done")
        }
    }


    @Test
    fun can_resume_after_stop() {
        runBlocking {
            val worker = PagedWorker(0, 3, 3)
            val manager = JobManager(listOf(worker), MockScheduler(), LoggerConsole(Warn, "manager"))
            manager.request(JobAction.Start , worker.id, "test")
            manager.request(JobAction.Stop  , worker.id, "test")
            manager.request(JobAction.Resume, worker.id, "test")
            println("done")
        }
    }
}