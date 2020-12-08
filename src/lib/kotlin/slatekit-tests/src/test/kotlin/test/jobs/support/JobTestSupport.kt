package test.jobs.support

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import slatekit.common.Status
import slatekit.common.Identity
import slatekit.common.SimpleIdentity
import slatekit.common.ids.Paired
import slatekit.common.log.LogLevel
import slatekit.common.log.LoggerConsole
import slatekit.jobs.*
import slatekit.jobs.support.Command
import slatekit.jobs.support.JobContext
import slatekit.jobs.support.pull
import slatekit.jobs.workers.Worker
import slatekit.jobs.workers.WorkerContext
import slatekit.jobs.workers.Workers
import test.jobs.samples.PagedWorker

interface JobTestSupport {


    fun run(numWorkers: Int, queue:Queue?, action:Action, operation:((Job) -> Unit)? = null ):Job{
        val manager = build(numWorkers, queue)
        runBlocking {
            manager.send(action)
            manager.pull()
            operation?.invoke(manager)
        }
        return manager
    }


    fun buildWorker(): Worker<Int> = PagedWorker(0, 5, 3)


    fun build(numWorkers:Int, queue: Queue?): Job {
        val workers = (1..numWorkers).map { buildWorker() }
        val id = (workers.first().id as SimpleIdentity)
        val jobId = id.copy(service = id.service + "-job")
        val ctx = JobContext(jobId, Channel(Channel.UNLIMITED), workers, queue = queue, scheduler = MockScheduler())
        val manager = slatekit.jobs.Job(ctx)
        return manager
    }


    fun ensure(workers: Workers, hasRun:Boolean, totalRuns:Long, totalPassed:Long, totalFailed:Long, id: Identity,
               status: Status, requestCount:Int, action: Action?, seconds:Long){
        val context: WorkerContext = workers[id]!!
        val runs = context.stats.calls
        val worker = context.worker

        // Status
        Assert.assertEquals(status, worker.status())

        // Calls
        Assert.assertEquals(hasRun, runs.hasRun())
        when(hasRun){
            true  -> Assert.assertNotNull(runs.lastTime())
            false -> Assert.assertNull   (runs.lastTime())
        }
        Assert.assertEquals(totalRuns  , runs.totalRuns()  )
        Assert.assertEquals(totalPassed, runs.totalPassed())
        Assert.assertEquals(totalFailed, runs.totalFailed())

        // Request count
        val coordinator = workers.ctx.channel as MockCoordinator
        Assert.assertEquals(requestCount, coordinator.requests.count())

        // Next request
        if(action != null) {
            val req = coordinator.requests.last() as Command.WorkerCommand
            Assert.assertEquals(id     , req.identity )
            Assert.assertEquals(action , req.action )
            Assert.assertEquals(seconds, req.seconds)
        }
    }

}