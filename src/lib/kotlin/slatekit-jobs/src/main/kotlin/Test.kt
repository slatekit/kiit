package slatekit.jobs

import kotlinx.coroutines.runBlocking
import slatekit.common.Status


fun main(args:Array<String>){

    val job = slatekit.jobs.Job()
    runBlocking {

        // Subscribe
        job.onChange { j -> println(j.id) }
        job.onStatus(Status.Complete) { j -> println(j.id) }

        // Subscribe
        job.workers.onChange { w -> println(w.id) }
        job.workers.onStatus(Status.Complete) { w -> println(w.id) }
    }
}