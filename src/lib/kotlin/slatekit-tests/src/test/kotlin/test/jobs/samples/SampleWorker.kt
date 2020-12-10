package test.jobs.samples

import slatekit.common.Identity
import slatekit.common.Status
import slatekit.jobs.*
import slatekit.jobs.workers.WorkResult
import slatekit.jobs.workers.Worker
import java.util.concurrent.atomic.AtomicInteger

class OneTimeWorker(val start:Int, val end:Int, id: Identity) : Worker<Int>(id) {


    constructor(start:Int, end:Int):this(start, end, Identity.test(OneTimeWorker::class.simpleName!!))


    private val current = AtomicInteger(start)
    private val audit = mutableListOf<String>()

    override suspend fun start() {
        audit.add("init")
        super.start()
    }


    override fun info(): List<Pair<String, String>> {
        return listOf(
            "id.name"  to this.id.name,
            "app.attemptStart" to this.start.toString(),
            "app.end"   to this.end.toString()
        )
    }


    override suspend fun work(task: Task): WorkResult {
        audit.add("work")
        (start .. end).forEach { current.incrementAndGet()  }
        return WorkResult.Done
    }


    override suspend fun done() {
        audit.add("done")
        super.done()
    }


    override suspend fun pause(reason: String?) {
    }


    override suspend fun stop(reason: String?) {
    }


    override suspend fun resume(reason: String?) {
    }


    fun currentValue():Int = current.get()
    fun audits():List<String> = audit.toList()
}


class PagedWorker(start:Int, val maxRuns:Int, val countsPerRun:Int, id: Identity? = null)
    : Worker<Int>( id ?: Identity.test(PagedWorker::class.simpleName!!)) {

    private val runs = AtomicInteger(0)
    private val counts = AtomicInteger(start)


    fun currentValue():Int = counts.get()


    override suspend fun work(task: Task): WorkResult {
        (0 until countsPerRun).forEach {
            counts.incrementAndGet()
        }
        val run = runs.incrementAndGet()
        return if(run < maxRuns) {
            WorkResult.More
        }
        else {
            WorkResult.Done
        }
    }


    override suspend fun pause(reason: String?) {
    }


    override suspend fun stop(reason: String?) {
    }


    override suspend fun resume(reason: String?) {
    }

}


class TestWorker(id: Identity? = null, val limit:Int = 10)
    : Worker<Int>( id ?: Identity.test(TestWorker::class.simpleName!!)) {

    val counts = AtomicInteger(0)
    val cycles = mutableMapOf<String, Boolean>()

    override suspend fun start() {
        cycles[Status.Started.name] = true
    }

    override suspend fun work(task: Task): WorkResult {
        val curr = counts.get()
        return if(curr < limit) {
            counts.incrementAndGet()
            WorkResult.More
        } else {
            WorkResult.Done
        }
    }

    override suspend fun pause(reason: String?) {
        cycles[Status.Paused.name] = true

    }

    override suspend fun resume(reason: String?) {
        cycles["Resumed"] = true

    }

    override suspend fun stop(reason: String?) {
        cycles[Status.Stopped.name] = true
    }

    override suspend fun done() {
        cycles[Status.Completed.name] = true
    }

    override suspend fun kill(reason: String?) {
        cycles[Status.Killed.name] = true
    }
}