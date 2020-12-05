package slatekit.jobs

import kotlinx.coroutines.runBlocking
import slatekit.common.Status

fun main(args: Array<String>) {

    lateinit var job: Job // = slatekit.jobs.Job()
    runBlocking {

        // Subscribe to any status change
        job.on { event -> println(event.id) }

        // Subscribe to completed status
        job.on(Status.Complete) { event -> println(event.id) }

        // Subscribe to any status change
        job.workers.on { event -> println(event.id) }

        // Subscribe to completed status
        job.workers.on(Status.Complete) { event -> println(event.id) }
    }
}
