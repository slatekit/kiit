package kiit.tasks

import kiit.common.DateTime
import kiit.common.DateTimes
import kiit.common.ext.toDateKey
import kiit.common.ids.UUIDs
import kiit.results.*
import kiit.results.builders.Outcomes


/**
 * Result of executing a specific task( adhoc, repeat, queued ).
 * This is the result of an instance of execution.
 */
data class TaskResult(
    val id: String,
    val action: Action,
    val version: String,
    val progress: Int,
    val processedAt: DateTime,
    val finishedAt: DateTime,
    val result: Outcome<*>,
    val message: String,
    val details: String,
    val state: String,
    val inputs: String,
    val output: String,
    val attempts: Int,
    val priority: Priority = Priority.Mid,
    val batch: String = "",
    val parent: String = "",
    val dated: Int = DateTimes.today().toDateKey().toInt(),
    val label: String = "",
    val tags: String = "",
    val createdAt: DateTime = DateTimes.now(),
    val createdBy: String = "",
    val updatedAt: DateTime = DateTimes.now(),
    val updatedBy: String = ""
) {
    /*
    companion object {


        fun update(task:TaskResult, progress:Int, state:String) : TaskResult {
            return task.copy(
                progress = progress,
                state = state,
                finishedAt = DateTimes.now(),
                result = Outcomes.pending(""),
                updatedAt = DateTimes.now()
            )
        }


        fun fail(task:TaskResult, state:String, result: Failure<Err>) : TaskResult {
            return task.copy(
                state = state,
                finishedAt = DateTimes.now(),
                result = result,
                updatedAt = DateTimes.now()
            )
        }


        fun done(task:TaskResult, state:String, result: Success<*>) : TaskResult {
            return task.copy(
                progress = 100,
                state = state,
                finishedAt = DateTimes.now(),
                result = result,
                output = result.value?.toString() ?: "",
                updatedAt = DateTimes.now()
            )
        }


        fun create(action: Action, id: String = UUIDs.create().value) : TaskResult {
            val pending = TaskResult(
                id = id,
                action = action,
                version = action.id.version,
                progress = 0,
                processedAt = DateTimes.now(),
                finishedAt = DateTimes.now(),
                result = Outcomes.pending(""),
                message = Codes.PENDING.name,
                details = Codes.PENDING.desc,
                inputs = "",
                output = "",
                state = "",
                attempts = 0
            )
            return pending
        }
    }
    */
}