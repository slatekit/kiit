package test.jobs

import slatekit.common.Identity
import slatekit.jobs.Pausable
import slatekit.jobs.Task
import slatekit.jobs.WorkState
import slatekit.jobs.Worker
import java.util.concurrent.atomic.AtomicInteger

class OneTimeWorker(val start:Int, val end:Int, id: Identity) : Worker<Int>(id), Pausable {


    constructor(start:Int, end:Int):this(start, end, Identity.test(OneTimeWorker::class.simpleName!!))


    private val current = AtomicInteger(start)
    private val flow = mutableListOf<String>()

    override suspend fun init() {
        flow.add("init")
        super.init()
    }


    override fun info(): List<Pair<String, String>> {
        return listOf(
            "id.name"  to this.id.name,
            "app.attemptStart" to this.start.toString(),
            "app.end"   to this.end.toString()
        )
    }


    override suspend fun work(task: Task): WorkState {
        flow.add("work")
        (start .. end).forEach { current.incrementAndGet()  }
        return WorkState.Done
    }


    override suspend fun done() {
        flow.add("done")
        super.done()
    }


    override suspend fun pause(reason: String?) {
    }


    override suspend fun stop(reason: String?) {
    }


    override suspend fun resume(reason: String?, task: Task): WorkState {
        return work(task)
    }


    fun currentValue():Int = current.get()
    fun currentFlows():List<String> = flow.toList()
}


class PagedWorker(start:Int, val maxRuns:Int, val countsPerRun:Int) : Worker<Int>(Identity.test(PagedWorker::class.simpleName!!)), Pausable {

    private val runs = AtomicInteger(0)
    private val counts = AtomicInteger(start)


    fun currentValue():Int = counts.get()


    override suspend fun work(task: Task): WorkState {
        (0 until countsPerRun).forEach {
            counts.incrementAndGet()
        }
        val run = runs.incrementAndGet()
        return if(run < maxRuns) {
            WorkState.More
        }
        else {
            WorkState.Done
        }
    }


    override suspend fun pause(reason: String?) {
    }


    override suspend fun stop(reason: String?) {
    }


    override suspend fun resume(reason: String?, task: Task): WorkState {
        return work(task)
    }

}