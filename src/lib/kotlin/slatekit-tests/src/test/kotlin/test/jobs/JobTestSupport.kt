package test.jobs

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import slatekit.common.Status
import slatekit.common.ids.Identity
import slatekit.common.log.Info
import slatekit.common.log.LoggerConsole
import slatekit.jobs.*

interface JobTestSupport {


    fun run(numWorkers: Int, queue:Queue?, action:JobAction, operation:((JobManager) -> Unit)? = null ):JobManager{
        val manager = build(numWorkers, queue)
        runBlocking {
            manager.request(action)
            manager.respond()
            operation?.invoke(manager)
        }
        return manager
    }


    fun buildWorker():Worker<Int> = PagedWorker(0, 5, 3)


    fun build(numWorkers:Int, queue: Queue?): JobManager {
        val workers = (1..numWorkers).map { buildWorker() }
        val logger = LoggerConsole(Info, "manager")
        val ids = JobId()
        val coordinator = MockCoordinatorWithChannel(logger, ids, Channel(Channel.UNLIMITED))
        val manager = JobManager(workers, queue, coordinator,  MockScheduler(), logger, ids)
        return manager
    }


    fun ensure(workers: Workers, hasRun:Boolean, totalRuns:Long, totalPassed:Long, totalFailed:Long, id: Identity,
                       status: Status, requestCount:Int, action: JobAction?, seconds:Long){
        val context: WorkerContext = workers.get(id)!!
        val runs = context.runs
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
            val req = coordinator.requests.last() as JobRequest.WorkRequest
            Assert.assertEquals(id     , req.workerId )
            Assert.assertEquals(action , req.action )
            Assert.assertEquals(seconds, req.seconds)
        }
    }

}