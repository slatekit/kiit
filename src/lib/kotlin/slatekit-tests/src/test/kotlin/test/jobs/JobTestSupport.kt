package test.jobs

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import slatekit.common.Status
import slatekit.common.Identity
import slatekit.common.SimpleIdentity
import slatekit.common.log.LogLevel
import slatekit.common.log.LoggerConsole
import slatekit.jobs.*
import slatekit.jobs.support.Command
import slatekit.jobs.support.JobId

interface JobTestSupport {


    fun run(numWorkers: Int, queue:Queue?, action:JobAction, operation:((Job) -> Unit)? = null ):Job{
        val manager = build(numWorkers, queue)
        runBlocking {
            manager.request(action)
            manager.respond()
            operation?.invoke(manager)
        }
        return manager
    }


    fun buildWorker():Worker<Int> = PagedWorker(0, 5, 3)


    fun build(numWorkers:Int, queue: Queue?): Job {
        val workers = (1..numWorkers).map { buildWorker() }
        val logger = LoggerConsole(LogLevel.Info, "manager")
        val ids = JobId()
        val coordinator = MockCoordinatorWithChannel(logger, ids, Channel(Channel.UNLIMITED))
        val id = (workers.first().id as SimpleIdentity)
        val jobId = id.copy(service = id.service + "-job")
        val manager = slatekit.jobs.Job(jobId,workers, queue, logger, ids, coordinator,  MockScheduler())
        return manager
    }


    fun ensure(workers: Workers, hasRun:Boolean, totalRuns:Long, totalPassed:Long, totalFailed:Long, id: Identity,
               status: Status, requestCount:Int, action: JobAction?, seconds:Long){
        val context: WorkerContext = workers.get(id)!!
        val runs = context.stats.calls
        val worker = context.worker

        // Status
        Assert.assertEquals(status, worker.status())

        // Calls
        Assert.assertEquals(hasRun, runs.hasRun())
        when(hasRun){
            true  -> Assert.assertNotNull  (runs.lastTime())
            false -> Assert.assertNull  (runs.lastTime())
        }
        Assert.assertEquals(totalRuns  , runs.totalRuns()  )
        Assert.assertEquals(totalPassed, runs.totalPassed())
        Assert.assertEquals(totalFailed, runs.totalFailed())

        // Request count
        val coordinator = workers.coordinator as MockCoordinator
        Assert.assertEquals(requestCount, coordinator.requests.count())

        // Next request
        if(action != null) {
            val req = coordinator.requests.last() as Command.WorkerCommand
            Assert.assertEquals(id     , req.workerId )
            Assert.assertEquals(action , req.action )
            Assert.assertEquals(seconds, req.seconds)
        }
    }

}