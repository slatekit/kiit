package slatekit.jobs

import kotlinx.coroutines.runBlocking
import slatekit.common.Status


fun main(args:Array<String>){

    lateinit var job:Job  // = slatekit.jobs.Job()
    runBlocking {

        // Subscribe to any status change
        job.subscribe { j -> println(j.id) }

        // Subscribe to completed status
        job.subscribe(Status.Complete) { j -> println(j.id) }

        // Subscribe to any status change
        job.workers.subscribe { w -> println(w.id) }

        // Subscribe to completed status
        job.workers.subscribe(Status.Complete) { w -> println(w.id) }
    }
}