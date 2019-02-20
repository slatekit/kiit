package slatekit.core.scheduler

import slatekit.common.*
import slatekit.common.log.Logs
import slatekit.common.metrics.Metrics
import slatekit.common.requests.Response
import slatekit.common.requests.toResponse
import slatekit.common.results.ResultCode
import slatekit.core.scheduler.core.ErrorMode
import slatekit.core.scheduler.core.RunMode
import slatekit.common.Status
import java.time.Duration
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.reflect.KFunction

typealias Result<T, E> = slatekit.common.Result<T, E>

/**
 * Wraps the java ScheduledExecutorService with diagnostic info
 */
class Scheduler(val settings: SchedulerSettings,
                val logs: Logs,
                val metrics: Metrics,
                val service: ScheduledExecutorService = Executors.newScheduledThreadPool(2)) {

    /**
     * Logger for this scheduler ( logger name="slatekit.core.scheduler.Scheduler" )
     */
    val logger = logs.getLogger(this.javaClass)


    /**
     * Diagnostic wrapper
     */
    val diagnostics = Diagnostics(metrics, logger)


    /**
     * Stores the enriched runnable as a task
     */
    private val commands = mutableMapOf<String, Task>()


    val offStates = listOf(Status.Complete, Status.Failed, Status.Stopped)


    /**
     * Forces an execution of a task ( used for manually on demand runs )
     */
    fun force(name: String): Result<Boolean, Exception> {
        return commands[name]?.let { task ->
            execute(TaskRequest(task, DateTime.now()), true)
        } ?: Failure(Exception("Could not find task with name: $name"))
    }

    /**
     * Schedule at a fixed rate using a simple lambda
     */
    fun scheduleAtFixedRate(name: String, delay: Long, period: Long, units: TimeUnit, call: () -> Unit) {
        store(name, period, call)
        service.scheduleAtFixedRate({ attempt(name) }, delay, period, units)
    }


    /**
     * Schedule at a fixed rate using an explicit task
     */
    fun scheduleAtFixedRate(task: Task, period: Long, units: TimeUnit) {
        commands[task.name] = task
        service.scheduleAtFixedRate({ attempt(task.name) }, task.delay, period, units)
    }


    /**
     * Schedule at a fixed rate using a method/function referenec
     */
    fun scheduleAtFixedRate(func: KFunction<Unit>, delay: Long, period: Long, units: TimeUnit) {
        val task = store(func.name, period, { func.call() })
        service.scheduleAtFixedRate({ attempt(task.name) }, delay, period, units)
    }


    /**
     * Pauses a task for the duration supplied
     */
    fun pause(name: String, duration: Duration): Result<Boolean, Exception> {
        return commands[name]?.let { task ->

            // Pause the task
            task.moveToState(Status.Paused)

            // Old status
            val oldState = task.status()

            // Move back to state after delay
            val timer = Timer("Timer")
            timer.schedule(object : TimerTask() {
                override fun run() {
                    changeStatus(oldState, TaskRequest(task, DateTime.now()), Success(true))
                }
            }, duration.toMillis())
            Success(true)
        } ?: Failure(Exception("Could not find task with name: $name"))
    }


    /**
     * Stops the task from running again
     */
    fun stop(name: String): Result<Boolean, Exception> {
        return commands[name]?.let { task ->
            task.moveToState(Status.Stopped)
            Success(true)
        } ?: Failure(Exception("Could not find task with name: $name"))
    }


    /**
     * Trigger a shutdown
     */
    fun shutdown() {
        service.shutdown()
    }


    /**
     * Shutdown immediately
     */
    fun shutdownNow() {
        service.shutdownNow()
    }


    private fun store(name: String, delay: Long, call: () -> Unit): Task {
        val task = Task(name, RunMode.Hybrid, ErrorMode.Moderate, delay, call)
        commands[name] = task
        return task
    }


    private fun attempt(name: String) {
        commands[name]?.let { task ->
            val status = task.status()
            val request = TaskRequest(task, DateTime.now())
            when (status) {
                is Status.Complete -> record(request, ResultCode.COMPLETED, "Task completed, skipping this run")
                is Status.Failed -> record(request, ResultCode.FAILED, "Task failed, skipping this run")
                is Status.Paused -> record(request, ResultCode.PAUSED, "Task paused, skipping this run")
                is Status.Stopped -> record(request, ResultCode.STOPPED, "Task is stopped, skipping this run")
                is Status.Running -> record(request, ResultCode.RUNNING, "Task is currently running")
                is Status.Starting -> execute(request, false)
                is Status.Idle -> execute(request, false)
                is Status.InActive -> execute(request, false)
            }
        }
    }

    /**
     * Records diagnostic events of the task request w/ the code supplied
     * Used only for non-execution attempts
     */
    private fun record(request: TaskRequest, code: Int, msg: String? = null) {
        diagnostics.record(this, request, Response(false, code, null, null))
    }


    /**
     * Runs a scheduled task with timestamp and diagnostics
     * @param request: The TaskRequest
     * @param forced : Whether this was manually forced
     */
    private fun execute(request: TaskRequest, forced: Boolean): Result<Boolean, Exception> {
        // Old status
        val oldState = request.task.status()

        // This
        val result = Result.attempt {

            // Move to running
            request.task.resume()

            // Execute
            request.task.call()

            true
        }

        // Diagnostics include logs, metrics, trackers(last result), events
        diagnostics.record(this, request, result.toResponse())

        // Move back to its proper state:
        changeStatus(oldState, request, result)

        return result
    }


    /**
     * Change the status based on old status and result of a run, factoring in
     * the ErrorMode the task is set up with.
     */
    private fun changeStatus(oldState: Status, request: TaskRequest, result: Result<Boolean, Exception>) {
        // Move back to its proper state:
        val task = request.task
        val isOff = offStates.contains(oldState)
        val failed = !result.success
        val success = result.success

        when {
            // CASE 1: Manually forced or edge case of running when off
            isOff -> task.moveToState(oldState)

            // CASE 2: Failed
            failed -> when(task.errorMode) {
                ErrorMode.Strict   -> task.moveToState(Status.Failed)
                ErrorMode.Flexible -> task.moveToState(Status.Idle)
                ErrorMode.Moderate -> pause(task.name, Duration.ofSeconds(task.delay * 3))
            }

            // CASE 3: Success!
            success -> task.moveToState(Status.Idle)
        }
    }
}