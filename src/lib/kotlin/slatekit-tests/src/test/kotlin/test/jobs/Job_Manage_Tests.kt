package test.jobs

import kotlinx.coroutines.runBlocking
import org.junit.Test
import slatekit.common.Status
import slatekit.jobs.*

class Job_Manage_Tests : JobTestSupport {

    @Test
    fun can_start_job() {
        run(1, null, Action.Start) {
            runBlocking {
                val worker = it.ctx.workers.first()
                ensure(it.workers, false, 0, 0, 0, worker.id, Status.InActive, 2, Action.Start, 0)
            }
        }
    }


    @Test
    fun can_process_job() {
        val manager = run(1, null, Action.Start)
        runBlocking {
            manager.poll() // Start worker
            val worker = manager.ctx.workers.first()
            ensure(manager.workers, true, 1, 1, 0, worker.id, Status.Running, 3, Action.Process, 0)
        }
    }


    @Test
    fun can_pause_job() {
        val manager = run(1, null, Action.Start)
        runBlocking {
            manager.send(Action.Pause)
            manager.poll() // Start worker
            manager.poll() // Job pause
            manager.poll() // Process 2nd time
            manager.poll() // Wrk pause
            (manager.coordinator as MockCoordinatorWithChannel).resume()
            val worker = manager.ctx.workers.first()
            ensure(manager.workers, true, 2, 2, 0, worker.id, Status.Paused, 7, Action.Resume, 0)
        }
    }


    @Test
    fun can_stop_job() {
        val manager = run(1, null, Action.Start)
        runBlocking {
            manager.send(Action.Stop)
            manager.poll() // Start worker
            manager.poll() // Job stop
            manager.poll() // Process 2nd time
            manager.poll() // Wrk stop
            (manager.coordinator as MockCoordinatorWithChannel).resume()
            val worker = manager.ctx.workers.first()
            ensure(manager.workers, true, 2, 2, 0, worker.id, Status.Stopped, 6, Action.Process, 0)
        }
    }


    @Test
    fun can_resume_job() {
        val manager = run(1, null, Action.Start)
        runBlocking {
            manager.send(Action.Pause)
            manager.poll() // Start worker
            manager.poll() // Job stop
            manager.poll() // Process 2nd time
            manager.poll() // Wrk pause
            (manager.coordinator as MockCoordinatorWithChannel).resume()
            manager.poll()
            manager.poll()
            val worker = manager.ctx.workers.first()
            ensure(manager.workers, true, 3, 3, 0, worker.id, Status.Running, 8, Action.Process, 0)
        }
    }
}