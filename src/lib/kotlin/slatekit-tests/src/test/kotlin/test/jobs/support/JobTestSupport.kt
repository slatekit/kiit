package test.jobs.support

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import slatekit.actors.Issuable
import slatekit.actors.Issuer
import slatekit.actors.Message
import slatekit.actors.Status
import slatekit.common.Identity
import slatekit.jobs.*
import slatekit.jobs.Context
import slatekit.jobs.Worker
import test.jobs.samples.TestWorker

interface JobTestSupport {
    fun setup(id: Identity, numWorkers: Int, queue: Queue?, builder: (Identity) -> Worker<*>, operation: (suspend (Manager, Issuer<Task>) -> Unit)?) {
        val mgr = create(numWorkers, id, builder, queue)
        runBlocking {
            val issuer = Issuer<Task>(mgr.channel, mgr as Issuable<Task>) { it.print() }
            operation?.invoke(mgr, issuer)
            mgr.close()
        }
    }

    fun create(numWorkers: Int, id: Identity, builder: (Identity) -> Worker<*>, queue: Queue?): Manager {
        val workers = (1..numWorkers).map { builder(id) }
        val channel = Channel<Message<Task>>(Channel.UNLIMITED)
        val ctx = Context(id, channel, workers, queue = queue, scheduler = MockScheduler())
        return Manager(ctx)
    }


    fun ensure(mgr:Manager, workers:Int, calls:Int, status: Status, isResumed:Boolean = false) {
        Assert.assertEquals(workers, mgr.jctx.workers.size)
        mgr.workers.getIds().forEach { workerId ->
            val wrkCtx = mgr.workers[workerId]!!
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