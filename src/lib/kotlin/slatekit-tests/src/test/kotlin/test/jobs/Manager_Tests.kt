package test.jobs

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.junit.Test
import slatekit.common.log.LoggerConsole
import slatekit.common.log.Warn
import slatekit.jobs.ChannelCoordinator
import slatekit.jobs.JobAction
import slatekit.jobs.JobManager
import slatekit.jobs.JobRequest

class Manager_Tests {

    @Test
    fun can_start() {
        val manager = build()
        runBlocking {
            manager.manageJob(JobRequest.TaskRequest(JobAction.Start), false)
            manager.respond()
            val status = manager.workers.workers.first().status()
            println(status)
        }
        println("done")
    }


    private fun build(): JobManager {
        val worker = PagedWorker(0, 3, 3)
        val logger = LoggerConsole(Warn, "manager")
        val coordinator = ChannelCoordinator(logger, Channel(Channel.UNLIMITED))
        val manager = JobManager(listOf(worker), coordinator,  MockScheduler(), logger)
        return manager
    }
}