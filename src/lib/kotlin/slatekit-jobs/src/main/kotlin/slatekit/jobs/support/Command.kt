package slatekit.jobs.support

import slatekit.common.Identity
import slatekit.jobs.JobAction

/**
 * Represents commands that can be sent to a Job to initiate an action.
 * All start | stop | pause | resume | etc operations on either the
 * job/queue/worker are communicated to the job by sending it an command
 * via a channel. These classes represent these commands.
 */
sealed class Command {
    abstract val id: Long
    abstract val uuid: String
    abstract val action: JobAction
    abstract val target: String
    abstract fun pairs(): List<Pair<String, String>>

    data class JobCommand(
        override val id: Long,
        override val uuid: String,
        override val action: JobAction
    ) : Command() {
        override val target: String = "job"

        override fun pairs(): List<Pair<String, String>> {
            return listOf(
                    "target" to target,
                    "id" to id.toString(),
                    "uuid" to uuid,
                    "action" to action.name
            )
        }
    }

    data class WorkerCommand(
        override val id: Long,
        override val uuid: String,
        override val action: JobAction,
        val workerId: Identity,
        val seconds: Long = 0,
        val desc: String?
    ) : Command() {

        override val target: String = "wrk"

        override fun pairs(): List<Pair<String, String>> {
            return listOf(
                    "target" to target,
                    "id" to id.toString(),
                    "uuid" to uuid,
                    "action" to action.name
            )
        }
    }
}
