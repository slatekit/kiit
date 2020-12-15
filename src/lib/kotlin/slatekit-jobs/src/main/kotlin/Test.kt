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
    lateinit var mgr: Manager // = slatekit.jobs.Job()
    lateinit var jobs: Jobs
    mgr.status()

    runBlocking {

        // Subscribe to any status change
        mgr.on { event -> println(event.id) }

        // Subscribe to completed status
        mgr.on(Status.Completed) { event -> println(event.id) }

        // Subscribe to any status change
        mgr.workers.on { event -> println(event.id) }

        // Subscribe to completed status
        mgr.workers.on(Status.Completed) { event -> println(event.id) }
    }
}
