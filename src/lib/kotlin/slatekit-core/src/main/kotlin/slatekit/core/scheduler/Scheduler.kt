package slatekit.core.scheduler

import slatekit.common.DateTime
import slatekit.common.diagnostics.Diagnostics
import slatekit.common.metrics.Metrics
import slatekit.common.toResponse
import slatekit.core.slatekit.core.scheduler.TaskRequest
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.reflect.KFunction
typealias Result<T,E> = slatekit.common.Result<T,E>

/**
 * Wraps the java ScheduledExecutorService with diagnostic info
 */
class Scheduler(val settings:SchedulerSettings,
                val metrics:Metrics,
                val service:ScheduledExecutorService = Executors.newScheduledThreadPool(2),
                val diagnostics:Diagnostics<TaskRequest>) {

    /**
     * Stores the enriched runnable as a task
     */
    private val commands = mutableMapOf<String, Task>()


    fun scheduleAtFixedRate(name:String, delay:Long, period:Long, units:TimeUnit, call:() -> Unit ) {
        store(name, call)
        service.scheduleAtFixedRate({ run(name) }, delay, period, units)
    }


    fun scheduleAtFixedRate(func: KFunction<Unit>, delay:Long, period:Long, units:TimeUnit) {
        val task = store(func.name, { func.call() })
        service.scheduleAtFixedRate({ run(task.name) }, delay, period, units)
    }


    fun shutdown() {
        service.shutdown()
    }


    fun shutdownNow() {
        service.shutdownNow()
    }


    private fun store(name:String, call:() -> Unit): Task {
        val task = Task(name, call)
        commands[name] = task
        return task
    }


    /**
     * Runs a scheduled task with timestamp and diagnostics
     */
    private fun run(name:String) {
        commands[name]?.let { task ->
            val request = TaskRequest(task, DateTime.now())
            val result = Result.attempt {  task.call() }
            val response = result.toResponse()
            diagnostics.record(this, request, response)
        }
    }
}