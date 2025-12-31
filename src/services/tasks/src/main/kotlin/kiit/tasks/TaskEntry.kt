package kiit.tasks

import kiit.common.DateTime
import kiit.common.DateTimes
import kiit.common.ids.UUIDs

/**
 *  @param version   : Schema version of the data / payload ( e.g. "1")
 *  @param uuid      : The id of the Task ( a UUID )
 *  @param origin    : The origin this task came from such as the service instane
 *  @param queue     : The name of the queue ( e.g. could be the QueueSourceMsg )
 *  @param name      : The name of this task ( to distinguish which worker can handle it ) e.g. "sendNewsLetter"
 *  @param data      : The inputs/data of the job as a json payload
 *  @param xid       : Serves as a correlation id
 *  @param tag       : Serves as a way to label this item
 *  @param entry     : An instance of the message/record from the queue. Used to acknowledge/complete/fail
 *  @param createdAt : Timestamp when this was created.
 *
 */
data class TaskEntry(
    override val version: String,
    override val uuid: String,
    override val origin: String,
    override val queue: String,
    override val name: String,
    override val data: String,
    override val xid: String = "",
    override val tag: String = "",
    override val createdAt: DateTime = DateTime.now(),
    override val entry: Any? = null
) : Task {


    override fun of(data:String): Task {
        return this.copy(uuid = UUIDs.create().value, data = data, createdAt = DateTimes.now())
    }

    override fun withNewId(newId:String): Task {
        return this.copy(uuid = newId)
    }


    companion object {
        val empty = TaskEntry("1", UUIDs.create().value, "", "", "", "")


        fun of(task:Task) : TaskEntry {
            return TaskEntry(
                version = task.version,
                uuid = task.uuid,
                origin = task.origin,
                queue = task.queue,
                name = task.name,
                data = task.data,
                xid = task.xid,
                tag = task.tag,
                createdAt = task.createdAt,
                entry = task.entry
            )
        }
    }
}