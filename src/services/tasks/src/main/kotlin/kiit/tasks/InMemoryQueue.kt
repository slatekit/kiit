package skky.tasks

import kiit.common.DateTimes
import kiit.common.Identity
import kiit.common.ids.UUIDs
import kiit.results.Try
import kiit.results.builders.Tries
import kiit.tasks.Events
import kiit.tasks.Priority
import kiit.tasks.Queue
import kiit.tasks.Task
import kiit.tasks.TaskEntry


class InMemoryTaskQueue(
    override val name: String,
    override val priority: Priority = Priority.Mid,
    val events: Events? = null
) : Queue {

    private val items = mutableListOf<TaskEntry>()
    val done = mutableListOf<Task>()
    val failed = mutableListOf<Task>()

    /**
     * Sends a payload / task into the queue
     */
    override suspend fun send(task: Task): Try<String> {
        val taskFinal = when(task is TaskEntry) {
            true -> task
            false -> TaskEntry.of(task)
        }
        items.add(taskFinal)
        events?.queuing?.emit(Events.EVENT_QUEUED_INSERT, taskFinal)
        return Tries.success(taskFinal.uuid)
    }


    override suspend fun send(value: String, attributes: Map<String, Any>?): Try<String> {
        val entry = TaskEntry(
            queue = name,
            data = value,
            version = attributes?.get("version") as? String ?: "",
            uuid = attributes?.get("id") as? String ?: UUIDs.create().value,
            origin = attributes?.get("source") as? String ?: "tests",
            name = attributes?.get("name") as? String ?: "",
            xid = attributes?.get("xid") as? String ?: "",
            tag = attributes?.get("tag") as? String ?: "",
            createdAt = DateTimes.now(),
            entry = null
        )
        return send(entry)
    }


    override suspend fun next(): Task? {
        return when (items.any()) {
            true -> items.removeAt(0)
            false -> null
        }
    }

    override suspend fun next(id: Identity): Task? {
        return when (items.any()) {
            true -> items.removeAt(0)
            false -> null
        }
    }

    override suspend fun done(task: Task) {
        done.add(task)
    }

    override suspend fun fail(task: Task) {
        failed.add(task)
    }
}