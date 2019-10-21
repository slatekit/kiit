package test.jobs

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import slatekit.common.Status
import slatekit.common.Identity
import slatekit.common.log.LoggerConsole
import slatekit.jobs.*
import slatekit.jobs.support.Command
import slatekit.jobs.support.JobId


class Workers_Tests {

    @Test
    fun can_start() {
        val workers = build()
        val worker = workers.all.first()
        runBlocking {
            workers.start(worker.id)
            ensure(workers,true, 1, 1, 0, worker.id, Status.Running, 1, JobAction.Process, 0)
        }
    }


    @Test
    fun can_pause() {
        val workers = build()
        val worker = workers.all.first()
        runBlocking {
            workers.start(worker.id)
            ensure(workers,true, 1, 1, 0, worker.id, Status.Running, 1, JobAction.Process, 0)
            workers.pause(worker.id, "test pause")
            ensure(workers,true, 1, 1, 0, worker.id, Status.Paused, 2, JobAction.Resume, 0)
        }
    }


    @Test
    fun can_stop() {
        val workers = build()
        val worker = workers.all.first()
        runBlocking {
            workers.start(worker.id)
            ensure(workers,true, 1, 1, 0, worker.id, Status.Running, 1, JobAction.Process, 0)
            workers.stop(worker.id, "test stop")
            ensure(workers,true, 1, 1, 0, worker.id, Status.Stopped, 1,null, 0)
        }
    }


    @Test
    fun can_resume() {
        val workers = build()
        val worker = workers.all.first()
        runBlocking {
            workers.start(worker.id)
            ensure(workers,true, 1, 1, 0, worker.id, Status.Running, 1, JobAction.Process, 0)
            workers.pause(worker.id, "test pause")
            ensure(workers,true, 1, 1, 0, worker.id, Status.Paused, 2, JobAction.Resume, 0)
            workers.resume(worker.id, "test resume")
            ensure(workers,true, 2, 2, 0, worker.id, Status.Running, 3, JobAction.Process, 0)
        }
    }


    @Test
    fun can_process() {
        val workers = build()
        val worker = workers.all.first()
        runBlocking {
            workers.start(worker.id)
            ensure(workers,true, 1, 1, 0, worker.id, Status.Running, 1, JobAction.Process, 0)
            workers.process(worker.id)
            ensure(workers,true, 2, 2, 0, worker.id, Status.Running, 2, JobAction.Process, 0)
        }
    }


    @Test
    fun can_complete() {
        val workers = build()
        val worker = workers.all.first()
        runBlocking {
            workers.start(worker.id)
            ensure(workers,true, 1, 1, 0, worker.id, Status.Running, 1, JobAction.Process, 0)
            (1 .. 4).forEach {
                workers.process(worker.id)
            }
            ensure(workers, true, 5, 5, 0, worker.id, Status.Complete, 4, JobAction.Process, 0)
        }
    }


    private fun ensure(workers:Workers, hasRun:Boolean, totalRuns:Long, totalPassed:Long, totalFailed:Long, id: Identity,
                       status: Status, requestCount:Int, action: JobAction?, seconds:Long){
        val context:WorkerContext = workers.get(id)!!
        val runs = context.stats.calls
        val worker = context.worker

        // Status
        Assert.assertEquals(worker.status(), status)

        // Calls
        Assert.assertEquals(runs.hasRun(), hasRun)
        when(hasRun){
            true  -> Assert.assertNotNull  (runs.lastTime())
            false -> Assert.assertNull  (runs.lastTime())
        }
        Assert.assertEquals(runs.totalRuns(), totalRuns)
        Assert.assertEquals(runs.totalPassed(), totalPassed)
        Assert.assertEquals(runs.totalFailed(), totalFailed)

        // Request count
        val coordinator = workers.coordinator as MockCoordinator
        Assert.assertEquals(coordinator.requests.count(), requestCount)

        // Next request
        if(action != null) {
            val req = coordinator.requests.last() as Command.WorkerCommand
            Assert.assertEquals(req.workerId, id)
            Assert.assertEquals(req.action, action)
            Assert.assertEquals(req.seconds, seconds)
        }
    }


    private fun build(): Workers {
        val worker = PagedWorker(0, 5, 2)
        val logger = LoggerConsole()
        val ids = JobId()
        val workers = Workers(listOf(worker), MockCoordinator(logger, ids), MockScheduler(), logger,ids, 20)
        return workers
    }
}