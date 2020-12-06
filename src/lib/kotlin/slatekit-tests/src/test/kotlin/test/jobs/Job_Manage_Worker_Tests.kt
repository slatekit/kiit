package test.jobs

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import slatekit.common.Status
import slatekit.jobs.*

class Job_Manage_Worker_Tests : JobTestSupport {

    @Test
    fun can_start_all_workers() {
        val manager = run(2, null, Action.Start)
        runBlocking {
            val worker1 = manager.ctx.workers.first()
            manager.respond() // Start worker1
            ensure(manager.workers, true, 1, 1, 0, worker1.id, Status.Running, 4, Action.Process, 0)

            val worker2 = manager.ctx.workers.last()
            manager.respond() // Start worker2val worker = manager.ctx.workers.first()
            ensure(manager.workers, true, 1, 1, 0, worker2.id, Status.Running, 5, Action.Process, 0)
        }
    }


    @Test
    fun can_pause_worker() {
        val manager = run(2, null, Action.Start)
        runBlocking {
            val worker1 = manager.ctx.workers.first()
            val worker2 = manager.ctx.workers.last()
            manager.request(Action.Pause, worker2.id, "test")
            manager.respond() // Start worker1
            manager.respond() // Start worker2
            manager.respond() // Pause worker1
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
            manager.request(Action.Pause, worker2.id, "test")
            manager.respond() // Start worker1
            manager.respond() // Start worker2
            manager.respond() // Pause worker1
            manager.respond() // Process worker1
            manager.respond() // Process worker2
            (manager.coordinator as MockCoordinatorWithChannel).resume()
            manager.respond() // Process worker1
            manager.respond() // Resume worker1
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
            manager.request(Action.Stop, worker2.id, "test")
            manager.respond() // Start worker1
            manager.respond() // Start worker2
            manager.respond() // Pause worker1
            Assert.assertEquals(Status.Running, worker1.status())
            Assert.assertEquals(Status.Stopped, worker2.status())
        }
    }
}