package test.jobs

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import slatekit.common.Status
import slatekit.jobs.*
import test.jobs.support.JobTestSupport
import test.jobs.support.MockCoordinatorWithChannel

class Job_Manage_Worker_Tests : JobTestSupport {

    @Test
    fun can_start_all_workers() {
        val job = run(2, null, Action.Start)
        runBlocking {
            // Start worker1
            val worker1 = job.ctx.workers.first()
            job.poll()
            ensure(job.workers, true, 1, 1, 0, worker1.id, Status.Running, 4, Action.Process, 0)

            // Start worker2val worker = manager.ctx.workers.first()
            val worker2 = job.ctx.workers.last()
            job.poll()
            ensure(job.workers, true, 1, 1, 0, worker2.id, Status.Running, 5, Action.Process, 0)
        }
    }


    @Test
    fun can_pause_worker() {
        val manager = run(2, null, Action.Start)
        runBlocking {
            val worker1 = manager.ctx.workers.first()
            val worker2 = manager.ctx.workers.last()
            manager.send(worker2.id, Action.Pause, "test")
            manager.poll() // Start worker1
            manager.poll() // Start worker2
            manager.poll() // Pause worker1
            Assert.assertEquals(Status.Running, worker1.status())
            Assert.assertEquals(Status.Paused, worker2.status())
        }
    }


    @Test
    fun can_resume_worker() {
        val manager = run(2, null, Action.Start)
        runBlocking {
            val worker1 = manager.ctx.workers.first()
            val worker2 = manager.ctx.workers.last()
            manager.send(worker2.id, Action.Pause, "test")
            manager.poll() // Start worker1
            manager.poll() // Start worker2
            manager.poll() // Pause worker1
            manager.poll() // Process worker1
            manager.poll() // Process worker2
            (manager.coordinator as MockCoordinatorWithChannel).resume()
            manager.poll() // Process worker1
            manager.poll() // Resume worker1
            Assert.assertEquals(Status.Running, worker1.status())
            Assert.assertEquals(Status.Running, worker2.status())
        }
    }


    @Test
    fun can_stop_worker() {
        val manager = run(2, null, Action.Start)
        runBlocking {
            val worker1 = manager.ctx.workers.first()
            val worker2 = manager.ctx.workers.last()
            manager.send(worker2.id, Action.Stop, "test")
            manager.poll() // Start worker1
            manager.poll() // Start worker2
            manager.poll() // Pause worker1
            Assert.assertEquals(Status.Running, worker1.status())
            Assert.assertEquals(Status.Stopped, worker2.status())
        }
    }
}