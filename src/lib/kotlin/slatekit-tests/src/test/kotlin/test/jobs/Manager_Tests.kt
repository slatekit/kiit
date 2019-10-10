package test.jobs

import kotlinx.coroutines.runBlocking
import org.junit.Test
import slatekit.common.log.LoggerConsole
import slatekit.common.log.Warn
import slatekit.jobs.JobAction
import slatekit.jobs.JobManager
import slatekit.jobs.JobRequest

class Manager_Tests {

    @Test
    fun can_start() {
        val worker = PagedWorker(0, 3, 3)
        val logger = LoggerConsole(Warn, "manager")
        val coordinator = MockCoordinator(logger)
        val manager = JobManager(listOf(worker), coordinator,  MockScheduler(), logger)
        runBlocking {
            val req = JobRequest.WorkRequest(JobAction.Start, worker.id, 0, "")
            manager.manageWorker(req, false)
        }
        println("done")
    }


    private fun build(){
        val worker = PagedWorker(0, 3, 3)
        val logger = LoggerConsole(Warn, "manager")
        val coordinator = MockCoordinator(logger)
        val manager = JobManager(listOf(worker), coordinator,  MockScheduler(), logger)
    }
}