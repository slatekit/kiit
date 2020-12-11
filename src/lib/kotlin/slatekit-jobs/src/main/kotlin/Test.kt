package slatekit.jobs

import kotlinx.coroutines.runBlocking
import slatekit.actors.Status

fun main(args: Array<String>) {

    /*
    1. managed
    2. workers
    3. job
    4. jobs
    5. executor
    6. channel control
    7. tests
     */
    lateinit var job: Job // = slatekit.jobs.Job()
    lateinit var jobs: Jobs
    job.status()

    runBlocking {

        // Subscribe to any status change
        job.on { event -> println(event.id) }

        // Subscribe to completed status
        job.on(Status.Completed) { event -> println(event.id) }

        // Subscribe to any status change
        job.workers.on { event -> println(event.id) }

        // Subscribe to completed status
        job.workers.on(Status.Completed) { event -> println(event.id) }
    }
}
