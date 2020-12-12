package test.jobs.support

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import slatekit.actors.Status
import slatekit.common.Identity
import slatekit.jobs.*
import slatekit.jobs.support.Command
import slatekit.jobs.Context
import slatekit.jobs.Worker
import test.jobs.samples.TestWorker

interface JobTestSupport {
    fun setup(id: Identity, numWorkers: Int, queue: Queue?, builder: (Identity) -> Worker<*>, operation: (suspend (Job) -> Unit)?) {
        val job = create(numWorkers, id, builder, queue)
        runBlocking {
            operation?.invoke(job)
            job.close()
        }
    }

    fun create(numWorkers: Int, id: Identity, builder: (Identity) -> Worker<*>, queue: Queue?): Job {
        val workers = (1..numWorkers).map { builder(id) }
        val channel = Channel<Command>(Channel.UNLIMITED)
        val ctx = Context(id, channel, workers, queue = queue, scheduler = MockScheduler())
        return Job(ctx)
    }


    fun ensure(job:Job, workers:Int, calls:Int, status:Status, isResumed:Boolean = false) {
        Assert.assertEquals(workers, job.ctx.workers.size)
        job.workers.getIds().forEach { workerId ->
            val wrkCtx = job.workers[workerId]!!
            val worker = wrkCtx.worker as TestWorker
            Assert.assertEquals(status, worker.status())
            Assert.assertEquals(calls, worker.counts.get())
            when(status){
                Status.Started   -> Assert.assertEquals(true, worker.cycles[Status.Started.name])
                Status.Paused    -> Assert.assertEquals(true, worker.cycles[Status.Paused.name])
                Status.Stopped   -> Assert.assertEquals(true, worker.cycles[Status.Stopped.name])
                Status.Completed -> Assert.assertEquals(true, worker.cycles[Status.Completed.name])
                Status.Killed    -> Assert.assertEquals(true, worker.cycles[Status.Killed.name])
                else -> {
                    if(isResumed) {
                        Assert.assertEquals(true, worker.cycles["Resumed"])
                    }
                }
            }
        }
    }

}