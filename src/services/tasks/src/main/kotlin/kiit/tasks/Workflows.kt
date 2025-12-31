package kiit.tasks

import kiit.common.DateTimes
import kiit.common.NOTE
import kiit.common.ids.UUIDs
import kiit.results.Err
import kiit.results.Outcome
import kiit.results.builders.Outcomes
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import java.util.concurrent.atomic.AtomicLong

object Workflows {

    fun getWorkflow(context: WorkContext, worker: Worker, scheduler: Scheduler? = null): Workflow {
        return when(context.action.mode) {
            Mode.Adhoc -> {
                AdHocWorkflow(context, worker)
            }
            Mode.Repeat -> {
                val finalScheduler = scheduler ?: DelayScheduler(context.scope)
                RepeatWorkflow(context, worker, finalScheduler)
            }
            Mode.Queued -> {
                QueueWorkflow(context, worker)
            }
        }
    }


    suspend fun ensure(worker: Worker, states:List<Status>, op: suspend () -> Outcome<Status> ):Outcome<Status> {
        if(worker.isFailed()) {
            return Outcomes.errored(Err.of("Worker state = failed"))
        }
        if(worker.isCompleted()) {
            return Outcomes.errored(Err.of("Worker state = completed"))
        }
        val state = worker.context.action.status.get()
        val currentStatus = state.status
        val matched = states.firstOrNull { it == currentStatus }
        return when(matched){
            null -> Outcomes.errored(Err.of("Worker state = ${state.status.name}"))
            else -> op()
        }
    }


    suspend fun moveTo(worker: Worker, newStatus:Status, states:List<Status>, op: suspend () -> Unit ):Outcome<Status> {
        val state = worker.context.action.status.get()
        val currentStatus = state.status
        val matched = states.firstOrNull { it == currentStatus }
        return when(matched) {
            null -> {
                Outcomes.invalid()
            }
            else -> {
                worker.move(newStatus, "")
                op()
                Outcomes.invalid()
            }
        }
    }
}


/**
 * Processor for work that is just done once or on demand
 */
class AdHocWorkflow(context: WorkContext, worker: Worker) : WorkflowBase(context, worker) {

    /**
     * This workflow only needs to work and immediately complete.
     * If the worker is paused or stopped, it must be actively started/resumed again first.
     */
    override suspend fun process(task: Task): Outcome<Status> {
        // Ensure we can only run in a valid worker status.
        val validStates = listOf(Status.InActive, Status.Ready)
        return ensure(validStates) {
            worker.move(Status.Running)
            worker.work(task)
            worker.move(Status.Completed)
            worker.move(Status.Ready)
            Outcomes.success(worker.status())
        }
    }
}


/**
 * Processor for work that is repeated on some schedule
 */
class RepeatWorkflow(context: WorkContext, worker: Worker, val scheduler: Scheduler)
    : WorkflowBase(context, worker) {
    private val counter = AtomicLong(0L)

    /**
     * This workflow needs to repeat
     */
    override suspend fun process(task: Task): Outcome<Status> {
        // Guard.
        if(worker.context.action.options.repeats == null) {
            return Outcomes.errored(Err.of("Schedule not setup"))
        }
        // Ensure we can only run in a valid worker status.
        val validStates = listOf(Status.InActive, Status.Ready, Status.Scheduled)
        val result = ensure(validStates) {
            worker.move(Status.Running)
            worker.work(task)
            worker.move(Status.Completed)
            counter.incrementAndGet()
            worker.move(Status.Scheduled)
            Outcomes.success(worker.status())
        }
        // Constraint: Schedule is basic for now
        val curr = DateTimes.now()
        val next = context.action.options.repeats!!.next(curr)
        println("curr=${curr}, next=${next}")

        // Repeat
        val options = worker.context.action.options
        val hasLimit = options.limit != null
        if(!hasLimit || counter.get() < options.limit!!) {
            scheduler.schedule(worker.context.id.name, curr, next) {
                // Have to generate a task with a new id
                val nextTask = task.withNewId(UUIDs.create().value)
                this.process(nextTask)
            }
        }
        return result
    }
}


/**
 * Processor for work that is based on a queue
 */
class QueueWorkflow(context: WorkContext, worker: Worker) : WorkflowBase(context, worker) {

    override suspend fun process(task: Task) : Outcome<Status> {
        // Guard.
        if(worker.context.queue == null) {
            return Outcomes.errored(Err.of("Queue not setup"))
        }
        // Ensure we can only run in a valid worker status.
        val validStates = listOf(Status.InActive, Status.Ready)
        ensure(validStates) {
            // Keeps track of processed for yielding purposes.
            var processed:Long = 0

            // Main worker loop
            val options = worker.context.action.options
            val hasLimit = options.limit != null
            if(canWork()) {
                // Move to running state.
                worker.move(Status.Running)
            }
            while(canWork() &&  (!hasLimit || (processed < options.limit!!))) {

                // Pull one at time.
                val qtask = context.queue?.next()
                when(qtask) {
                    null -> {
                        NOTE.IMPLEMENT("tasks", "Configurable delay")
                        delay(5000)
                        yield()
                    }
                    else -> {
                        worker.work(qtask)
                        processed += 1
                    }
                }
                // Stop processing ( only useful for dry runs / tests )
                if(hasLimit && processed >= options.limit!!) {
                    worker.move(Status.Ready)
                }
                // Periodic yield to let other coroutines run
                if(processed > options.yieldAt) {
                    processed = 0
                    yield()
                }
            }
            Outcomes.success(worker.status())
        }
        return Outcomes.errored()
    }

    private fun canWork() : Boolean = worker.isRunning() || worker.isInActive() || worker.isReady()
}