package test.jobs

import slatekit.common.DateTime
import slatekit.common.log.Logger
import slatekit.jobs.Scheduler
import slatekit.jobs.Coordinator
import slatekit.jobs.JobRequest


class MockScheduler : Scheduler{
    override suspend fun schedule(time: DateTime, op: suspend () -> Unit) {
        op()
    }
}


class MockCoordinator(override val logger: Logger) : Coordinator {

    val requests = mutableListOf<JobRequest>()

    override suspend fun request(jobRequest: JobRequest) {
        requests.add(jobRequest)
    }

    override suspend fun respondOne(): JobRequest? {
        return requests.firstOrNull()
    }


    override suspend fun respond(operation:suspend (JobRequest) -> Unit ) {
        for(request in requests){
            operation(request)
        }
    }
}