package test.jobs

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import slatekit.common.Status
import slatekit.jobs.*

class Manage_Job_Tests : JobTestSupport {

    @Test
    fun can_start_job() {
        run(1, null, JobAction.Start) {
            runBlocking {
                val worker = it.workers.all.first()
                ensure(it.workers, false, 0, 0, 0, worker.id, Status.InActive, 2, JobAction.Start, 0)
            }
        }
    }


    @Test
    fun can_process_job() {
        val manager = run(1, null, JobAction.Start)
        runBlocking {
            manager.respond() // Start worker
            val worker = manager.workers.all.first()
            ensure(manager.workers, true, 1, 1, 0, worker.id, Status.Running, 3, JobAction.Process, 0)
        }
    }


    @Test
    fun can_pause_job() {
        val manager = run(1, null, JobAction.Start)
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
        val manager = run(1, null, JobAction.Start)
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
        val manager = run(1, null, JobAction.Start)
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
}