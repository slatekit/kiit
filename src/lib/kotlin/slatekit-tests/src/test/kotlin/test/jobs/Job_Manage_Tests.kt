package test.jobs

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import slatekit.common.Identity
import slatekit.common.Status
import slatekit.jobs.*
import slatekit.jobs.support.Command
import slatekit.jobs.support.JobContext
import slatekit.jobs.workers.Worker
import slatekit.jobs.workers.WorkerContext
import slatekit.jobs.slatekit.jobs.Workers
import test.jobs.samples.BatchWorker
import test.jobs.samples.PagedWorker
import test.jobs.support.JobTestSupport
import test.jobs.support.MockScheduler

class Job_Manage_Tests : JobTestSupport {

    private val ID = Identity.job("tests", "job-manager")
    fun setup(id: Identity, numWorkers: Int, queue: Queue?, action: Action?, builder: (Identity) -> Worker<*>, operation: (suspend (Job) -> Unit)?) {
        val job = create(numWorkers, id, builder, queue)
        runBlocking {
            action?.let {
                job.send(action)
                job.pull(numWorkers + 1)
            }
            operation?.invoke(job)
            job.kill()
        }
    }

    fun create(numWorkers: Int, id: Identity, builder: (Identity) -> Worker<*>, queue: Queue?): Job {
        val workers = (1..numWorkers).map { builder(id) }
        val channel = Channel<Command>(Channel.UNLIMITED)
        val ctx = JobContext(id, channel, workers, queue = queue, scheduler = MockScheduler())
        return Job(ctx)
    }

    fun worker(id: Identity): Worker<Int> = PagedWorker(0, 5, 3, id)

    fun check(workers: Workers, id: Identity, status: Status) {
        val context: WorkerContext = workers[id]!!
        val worker = context.worker

        // Status
        Assert.assertEquals(status, worker.status())
    }

    @Test
    fun can_create_job() {
        setup(ID, 1, null, null, { id -> worker(id) }) { job ->
            job.workers.getIds().forEach { workerId ->
                check(job.workers, workerId, Status.InActive)
            }
        }
    }

    @Test
    fun can_start_job() {
        setup(ID, 1, null, Action.Start, { id -> BatchWorker(id) }) { job ->
            job.workers.getIds().forEach { workerId ->
                val wrkCtx = job.workers[workerId]!!
                val worker = wrkCtx.worker as BatchWorker
                check(job.workers, workerId, Status.Running)
                Assert.assertEquals(1, wrkCtx.stats.calls.totalRuns())
                Assert.assertEquals(1, worker.counts.get())
            }
        }
    }

    @Test
    fun can_process_job() {
        setup(ID, 1, null, Action.Start, { id -> BatchWorker(id) }) { job ->
            // Process :2
            // 1. command 1 = job
            // 2. command 2 = wrk ( to process )
            job.process()
            job.pull(2)
            job.workers.getIds().forEach { workerId ->
                val wrkCtx = job.workers[workerId]!!
                val worker = wrkCtx.worker as BatchWorker
                check(job.workers, workerId, Status.Running)
                Assert.assertEquals(2, wrkCtx.stats.calls.totalRuns())
                Assert.assertEquals(2, worker.counts.get())
            }
        }
    }


    @Test
    fun can_pause_job() {
        setup(ID, 1, null, Action.Start, { id -> BatchWorker(id, limit = 10) }) { job ->
            // Process :2
            // 1. command 1 = job
            // 2. command 2 = wrk ( to process )
            job.pause()
            job.poll()
            job.workers.getIds().forEach { workerId ->
                val wrkCtx = job.workers[workerId]!!
                val worker = wrkCtx.worker as BatchWorker
                check(job.workers, workerId, Status.Paused)
                Assert.assertEquals(3, wrkCtx.stats.calls.totalRuns())
                Assert.assertEquals(3, worker.counts.get())
            }
        }
    }


    @Test
    fun can_stop_job() {
        val job = run(1, null, Action.Start)
        runBlocking {
            job.send(Action.Stop)
            job.pull(5)
            job.ctx.channel.close()
            val worker = job.ctx.workers.first()
            ensure(job.workers, true, 2, 2, 0, worker.id, Status.Stopped, 6, Action.Process, 0)
        }
    }


    @Test
    fun can_resume_job() {
        val job = run(1, null, Action.Start)
        runBlocking {
            job.send(Action.Pause)
            job.pull() // Start worker
            job.pull() // Job stop
            job.pull() // Process 2nd time
            job.pull() // Wrk pause
            job.ctx.channel.close()
            job.pull()
            job.pull()
            val worker = job.ctx.workers.first()
            ensure(job.workers, true, 3, 3, 0, worker.id, Status.Running, 8, Action.Process, 0)
        }
    }
}