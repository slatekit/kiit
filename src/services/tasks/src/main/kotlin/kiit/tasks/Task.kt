package kiit.tasks

import kiit.common.DateTime
import kiit.common.DateTimes
import kiit.common.ids.UUIDs

/**
 * Task represents a unit-of work ( a work-item  ) that is handled by a Worker.
 * A task is stored as a single message in queue or a record in a database.
 *
 * @sample:
 *  uuid      = "ABC123",
 *  version   = "v1"
 *  origin    = "server1"
 *  queue     = "notifications",
 *  name      = "account.signup.sendWelcomeEmail",
 *  data      = "JSON data...",
 *  xid       = "user123"
 *  tag       = "unit-test"
 *  createdAt = "2025-04-27T12:00:00PM"
 *  entry     = e.g. @see[kiit.tasks.TaskEntry]
 */
interface Task {
    val uuid: String
    val version: String
    val origin: String
    val queue: String
    val name: String
    val data: String
    val xid: String
    val tag: String
    val createdAt: DateTime
    val entry: Any?

    fun of(data:String): Task
    fun withNewId(newId: String): Task

    companion object {
        fun of(version: String, origin: String, queue: String, name: String, data: String,
               uuid:String? = null, xid:String? = null, tag:String? = null): TaskEntry {
            return TaskEntry(
                version = version,
                uuid = uuid ?: UUIDs.create().value,
                origin = origin,
                queue = queue,
                name = name,
                data = data,
                xid = xid ?: "",
                tag = tag ?: "",
                createdAt = DateTimes.now(),
                entry = null
            )
        }

    }
}

