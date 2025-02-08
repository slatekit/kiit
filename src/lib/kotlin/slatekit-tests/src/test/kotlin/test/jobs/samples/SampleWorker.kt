package test.jobs.samples

import kiit.common.Identity
import kiit.actors.Status
import kiit.jobs.WResult
import kiit.jobs.*
import kiit.jobs.Worker
import java.util.concurrent.atomic.AtomicInteger

class OneTimeWorker(val start:Int, val end:Int, id: Identity) : Worker<Int>(id) {


    constructor(start:Int, end:Int):this(start, end, Identity.test("kiit", OneTimeWorker::class.simpleName!!))


    private val current = AtomicInteger(start)
    private val audit = mutableListOf<String>()

    override suspend fun started() {
        audit.add("init")
        super.started()
    }


    override fun info(): List<Pair<String, String>> {
        return listOf(
            "id.name"  to this.id.name,
            "app.attemptStart" to this.start.toString(),
            "app.end"   to this.end.toString()
        )
    }


    override suspend fun work(task: Task): WResult {
        audit.add("work")
        (start .. end).forEach { current.incrementAndGet()  }
        return WResult.Done
    }


    override suspend fun onCompleted(note:String?) {
        audit.add("done")
        super.onCompleted(note)
    }


    override suspend fun onPaused(reason: String?) {
    }


    override suspend fun onStopped(reason: String?) {
    }


    override suspend fun onResumed(reason: String?) {
    }


    fun currentValue():Int = current.get()
    fun audits():List<String> = audit.toList()
}


class PagedWorker(start:Int, val maxRuns:Int, val countsPerRun:Int, id: Identity? = null)
    : Worker<Int>( id ?: Identity.test("kiit", PagedWorker::class.simpleName!!)) {

    private val runs = AtomicInteger(0)
    private val counts = AtomicInteger(start)


    fun currentValue():Int = counts.get()


    override suspend fun work(task: Task): WResult {
        (0 until countsPerRun).forEach {
            counts.incrementAndGet()
        }
        val run = runs.incrementAndGet()
        return if(run < maxRuns) {
            WResult.More
        }
        else {
            WResult.Done
        }
    }


    override suspend fun onPaused(reason: String?) {
    }


    override suspend fun onStopped(reason: String?) {
    }


    override suspend fun onResumed(reason: String?) {
    }

}


class TestWorker(id: Identity? = null, val limit:Int = 10)
    : Worker<Int>( id ?: Identity.test("kiit", TestWorker::class.simpleName!!)) {

    val counts = AtomicInteger(0)
    val cycles = mutableMapOf<String, Boolean>()

    override suspend fun started() {
        cycles[Status.Started.name] = true
    }

    override suspend fun work(task: Task): WResult {
        val curr = counts.get()
        return if(curr < limit) {
            counts.incrementAndGet()
            WResult.More
        } else {
            WResult.Done
        }
    }

    override suspend fun onPaused(reason: String?) {
        cycles[Status.Paused.name] = true

    }

    override suspend fun onResumed(reason: String?) {
        cycles["Resumed"] = true

    }

    override suspend fun onStopped(reason: String?) {
        cycles[Status.Stopped.name] = true
    }

    override suspend fun onCompleted(note:String?) {
        cycles[Status.Completed.name] = true
    }

    override suspend fun onKilled(reason: String?) {
        cycles[Status.Killed.name] = true
    }
}