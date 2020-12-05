package slatekit.jobs

import kotlinx.coroutines.runBlocking
import slatekit.common.Status

fun main(args: Array<String>) {

    lateinit var job: Job // = slatekit.jobs.Job()
    runBlocking {

        // Subscribe to any status change
        job.on { j -> println(j.id) }

        // Subscribe to completed status
        job.on(Status.Complete) { j -> println(j.id) }

        // Subscribe to any status change
        job.workers.on { w -> println(w.id) }

        // Subscribe to completed status
        job.workers.on(Status.Complete) { w -> println(w.id) }
    }
}
