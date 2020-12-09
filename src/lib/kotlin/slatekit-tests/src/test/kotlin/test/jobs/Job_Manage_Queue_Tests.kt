package test.jobs

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import slatekit.common.Status
import slatekit.core.queues.InMemoryQueue
import slatekit.core.queues.QueueStringConverter
import slatekit.core.queues.WrappedAsyncQueue
import slatekit.integration.jobs.JobQueue
import slatekit.jobs.*
import slatekit.jobs.support.pull
import test.jobs.support.JobTestSupport
import test.jobs.support.MockCoordinatorWithChannel


class Job_Manage_Queue_Tests : JobTestSupport {

    fun sampleQueue():Queue{
        val source = InMemoryQueue<String>("q1", QueueStringConverter())
        val queue = JobQueue(source.name, Priority.Mid, WrappedAsyncQueue(source))
        return queue
    }


    @Test
    fun can_start_all_workers() {
        runBlocking {
            val queue = sampleQueue()
            (1..10).forEach { queue.send(it.toString()) }
            val manager = run(2, queue, Action.Start)
            runBlocking {
                val worker1 = manager.ctx.workers.first()
                manager.pull() // Start worker1
                ensure(manager.workers, true, 1, 1, 0, worker1.id, Status.Running, 4, Action.Process, 0)

                val worker2 = manager.ctx.workers.last()
                manager.pull() // Start worker2val worker = manager.workers.all.first()
                ensure(manager.workers, true, 1, 1, 0, worker2.id, Status.Running, 5, Action.Process, 0)
            }
        }
    }


    @Test
    fun can_pause_worker() {
        runBlocking {
            val queue = sampleQueue()
            (1..10).forEach { queue.send(it.toString()) }
            val manager = run(2, queue, Action.Start)
            runBlocking {
                val worker1 = manager.ctx.workers.first()
                val worker2 = manager.ctx.workers.last()
                manager.send(worker2.id, Action.Pause, "test")
                manager.pull() // Start worker1
                manager.pull() // Start worker2
                manager.pull() // Pause worker1
                Assert.assertEquals(Status.Running, worker1.status())
                Assert.assertEquals(Status.Paused, worker2.status())
            }
        }
    }


    @Test
    fun can_pause_worker_due_to_empty_queue() {
        runBlocking {
            val queue = sampleQueue()
            (1..2).forEach { queue.send(it.toString()) }
            val manager = run(2, queue, Action.Start)
            runBlocking {

                manager.pull() // Start worker1
                manager.pull() // Start worker2

                val worker1 = manager.ctx.workers.first()
                val worker2 = manager.ctx.workers.last()
                println("worker 1 id: " + worker1.id.id)
                println("worker 2 id: " + worker2.id.id)

                // Backoff at 2, status is
                val worker1BackoffBefore = manager.workers.get(worker1.id)!!.backoffs.curr()
                val worker2BackoffBefore = manager.workers.get(worker2.id)!!.backoffs.curr()
                Assert.assertEquals(2, worker1BackoffBefore)
                Assert.assertEquals(2, worker2BackoffBefore)
                Assert.assertEquals(Status.Running, worker1.status())
                Assert.assertEquals(Status.Running, worker2.status())

                // Backed off at, moved to 4
                manager.pull() // Backoff worker1
                manager.pull() // Backoff worker1
                val worker1Backoff2 = manager.workers.get(worker1.id)!!.backoffs.curr()
                val worker2Backoff2 = manager.workers.get(worker2.id)!!.backoffs.curr()
                Assert.assertEquals(4, worker1Backoff2)
                Assert.assertEquals(4, worker2Backoff2)
                Assert.assertEquals(Status.Paused, worker1.status())
                Assert.assertEquals(Status.Paused, worker2.status())

                // Back off at 4 moved to 8
                manager.pull() // Resume worker 1
                manager.pull() // Resume worker 2
                val worker1Backoff3 = manager.workers.get(worker1.id)!!.backoffs.curr()
                val worker2Backoff3 = manager.workers.get(worker2.id)!!.backoffs.curr()
                Assert.assertEquals(8, worker1Backoff3)
                Assert.assertEquals(8, worker2Backoff3)
                Assert.assertEquals(Status.Paused, worker1.status())
                Assert.assertEquals(Status.Paused, worker2.status())
                Assert.assertEquals("Backoff", worker1.note())
                Assert.assertEquals("Backoff", worker2.note())

                // Ensure its back to running
                (3..4).forEach { queue.send(it.toString()) }
                manager.pull() // Resume worker 1
                manager.pull() // Resume worker 2
                val worker1Backoff4 = manager.workers.get(worker1.id)!!.backoffs.curr()
                val worker2Backoff4 = manager.workers.get(worker2.id)!!.backoffs.curr()
                Assert.assertEquals(2, worker1Backoff4)
                Assert.assertEquals(2, worker2Backoff4)
                Assert.assertEquals(Status.Running, worker1.status())
                Assert.assertEquals(Status.Running, worker2.status())
            }
        }
    }


    @Test
    fun can_resume_worker() {
        runBlocking {
            val queue = sampleQueue()
            (1..10).forEach { queue.send(it.toString()) }
            val manager = run(2, queue, Action.Start)
            runBlocking {
                val worker1 = manager.ctx.workers.first()
                val worker2 = manager.ctx.workers.last()
                manager.send(worker2.id, Action.Pause, "test")
                manager.pull() // Start worker1
                manager.pull() // Start worker2
                manager.pull() // Pause worker1
                manager.pull() // Process worker1
                manager.pull() // Process worker2
                manager.pull() // Process worker1
                manager.pull() // Resume worker1
                Assert.assertEquals(Status.Running, worker1.status())
                Assert.assertEquals(Status.Running, worker2.status())
            }
        }
    }


    @Test
    fun can_stop_worker() {
        runBlocking {
            val queue = sampleQueue()
            (1..10).forEach { queue.send(it.toString()) }
            val manager = run(2, queue, Action.Start)
            runBlocking {
                val worker1 = manager.ctx.workers.first()
                val worker2 = manager.ctx.workers.last()
                manager.send(worker2.id, Action.Stop, "test")
                manager.pull() // Start worker1
                manager.pull() // Start worker2
                manager.pull() // Pause worker1
                Assert.assertEquals(Status.Running, worker1.status())
                Assert.assertEquals(Status.Stopped, worker2.status())
            }
        }
    }
}