package test.jobs

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import slatekit.common.Status
import slatekit.common.ids.Identity
import slatekit.common.log.Info
import slatekit.common.log.LoggerConsole
import slatekit.jobs.*

class Manager_Tests {

    @Test
    fun can_start_job() {
        run(JobAction.Start) {
            runBlocking {
                val worker = it.workers.all.first()
                ensure(it.workers, false, 0, 0, 0, worker.id, Status.InActive, 2, JobAction.Start, 0)
            }
        }
    }


    @Test
    fun can_process_job() {
        val manager = run(JobAction.Start)
        runBlocking {
            manager.respond() // Start worker
            val worker = manager.workers.all.first()
            ensure(manager.workers, true, 1, 1, 0, worker.id, Status.Running, 3, JobAction.Process, 0)
        }
    }


    @Test
    fun can_pause_job() {
        val manager = run(JobAction.Start)
        runBlocking {
            manager.request(JobAction.Pause)
            manager.respond() // Start worker
            manager.respond() // Job pause
            manager.respond() // Process 2nd time
            manager.respond() // Wrk pause
            (manager.coordinator as MockCoordinatorWithChannel).resume()
            val worker = manager.workers.all.first()
            ensure(manager.workers, true, 2, 2, 0, worker.id, Status.Paused, 7, JobAction.Resume, 0)
        }
    }


    @Test
    fun can_stop_job() {
        val manager = run(JobAction.Start)
        runBlocking {
            manager.request(JobAction.Stop)
            manager.respond() // Start worker
            manager.respond() // Job stop
            manager.respond() // Process 2nd time
            manager.respond() // Wrk stop
            (manager.coordinator as MockCoordinatorWithChannel).resume()
            val worker = manager.workers.all.first()
            ensure(manager.workers, true, 2, 2, 0, worker.id, Status.Stopped, 6, JobAction.Process, 0)
        }
    }


    @Test
    fun can_resume_job() {
        val manager = run(JobAction.Start)
        runBlocking {
            manager.request(JobAction.Pause)
            manager.respond() // Start worker
            manager.respond() // Job stop
            manager.respond() // Process 2nd time
            manager.respond() // Wrk pause
            (manager.coordinator as MockCoordinatorWithChannel).resume()
            manager.respond()
            manager.respond()
            val worker = manager.workers.all.first()
            ensure(manager.workers, true, 3, 3, 0, worker.id, Status.Running, 8, JobAction.Process, 0)
        }
    }


    private fun run(action:JobAction, operation:((JobManager) -> Unit)? = null ):JobManager{
        val manager = build()
        runBlocking {
            manager.request(action)
            manager.respond()
            operation?.invoke(manager)
        }
        return manager
    }


    private fun build(): JobManager {
        val worker = PagedWorker(0, 5, 3)
        val logger = LoggerConsole(Info, "manager")
        val ids = JobId()
        val coordinator = MockCoordinatorWithChannel(logger, ids, Channel(Channel.UNLIMITED))
        val manager = JobManager(listOf(worker), coordinator,  MockScheduler(), logger, ids)
        return manager
    }


    private fun ensure(workers: Workers, hasRun:Boolean, totalRuns:Long, totalPassed:Long, totalFailed:Long, id: Identity,
                       status: Status, requestCount:Int, action: JobAction?, seconds:Long){
        val context: WorkerContext = workers.get(id)!!
        val runs = context.runs
        val worker = context.worker

        // Status
        Assert.assertEquals(status, worker.status())

        // Calls
        Assert.assertEquals(hasRun, runs.hasRun())
        when(hasRun){
            true  -> Assert.assertNotNull  (runs.lastTime())
            false -> Assert.assertNull  (runs.lastTime())
        }
        Assert.assertEquals(totalRuns  , runs.totalRuns()  )
        Assert.assertEquals(totalPassed, runs.totalPassed())
        Assert.assertEquals(totalFailed, runs.totalFailed())

        // Request count
        val coordinator = workers.coordinator as MockCoordinator
        Assert.assertEquals(requestCount, coordinator.requests.count())

        // Next request
        if(action != null) {
            val req = coordinator.requests.last() as JobRequest.WorkRequest
            Assert.assertEquals(id     , req.workerId )
            Assert.assertEquals(action , req.action )
            Assert.assertEquals(seconds, req.seconds)
        }
    }
}