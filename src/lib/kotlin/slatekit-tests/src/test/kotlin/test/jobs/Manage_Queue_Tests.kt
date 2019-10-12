package test.jobs

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import slatekit.common.Status
import slatekit.common.queues.QueueSourceInMemory
import slatekit.common.queues.QueueStringConverter
import slatekit.jobs.*


//class Manage_Queue_Tests : JobTestSupport {
//
//    fun sampleQueue():Queue{
//        val source = QueueSourceInMemory<String>("q1", QueueStringConverter())
//        val queue = Queue(source.name, Priority.Medium, source)
//        return queue
//    }
//
//
//    @Test
//    fun can_start_all_workers() {
//        val manager = run(2, sampleQueue(), JobAction.Start)
//        runBlocking {
//            val worker1 = manager.workers.all.first()
//            manager.respond() // Start worker1
//            ensure(manager.workers, true, 1, 1, 0, worker1.id, Status.Running, 4, JobAction.Process, 0)
//
//            val worker2 = manager.workers.all.last()
//            manager.respond() // Start worker2val worker = manager.workers.all.first()
//            ensure(manager.workers, true, 1, 1, 0, worker2.id, Status.Running, 5, JobAction.Process, 0)
//        }
//    }
//
//
//    @Test
//    fun can_pause_worker() {
//        val manager = run(2, sampleQueue(), JobAction.Start)
//        runBlocking {
//            val worker1 = manager.workers.all.first()
//            val worker2 = manager.workers.all.last()
//            manager.request(JobAction.Pause, worker2.id, "test")
//            manager.respond() // Start worker1
//            manager.respond() // Start worker2
//            manager.respond() // Pause worker1
//            Assert.assertEquals(Status.Running, worker1.status())
//            Assert.assertEquals(Status.Paused, worker2.status())
//        }
//    }
//
//
//    @Test
//    fun can_resume_worker() {
//        val manager = run(2, sampleQueue(), JobAction.Start)
//        runBlocking {
//            val worker1 = manager.workers.all.first()
//            val worker2 = manager.workers.all.last()
//            manager.request(JobAction.Pause, worker2.id, "test")
//            manager.respond() // Start worker1
//            manager.respond() // Start worker2
//            manager.respond() // Pause worker1
//            manager.respond() // Process worker1
//            manager.respond() // Process worker2
//            (manager.coordinator as MockCoordinatorWithChannel).resume()
//            manager.respond() // Process worker1
//            manager.respond() // Resume worker1
//            Assert.assertEquals(Status.Running, worker1.status())
//            Assert.assertEquals(Status.Running, worker2.status())
//        }
//    }
//
//
//    @Test
//    fun can_stop_worker() {
//        val manager = run(2, sampleQueue(), JobAction.Start)
//        runBlocking {
//            val worker1 = manager.workers.all.first()
//            val worker2 = manager.workers.all.last()
//            manager.request(JobAction.Stop, worker2.id, "test")
//            manager.respond() // Start worker1
//            manager.respond() // Start worker2
//            manager.respond() // Pause worker1
//            Assert.assertEquals(Status.Running, worker1.status())
//            Assert.assertEquals(Status.Stopped, worker2.status())
//        }
//    }
//}