package slatekit.jobs

import slatekit.common.DateTime
import slatekit.common.Identity
import slatekit.actors.Status

/**
 * @param id    : Identity of the job/worker @see[slatekit.common.Identity]
 * @param name  : Name of the event ( e.g. STATE_CHANGED )
 * @param source: Name of the source either "job" or "worker"
 * @param status: Status of the worker
 * @param queue : Name of the queue being run
 * @param info  : Additional info/diagnostics provided by the worker
 */
data class Event(val id: Identity,
                 val name: String,
                 val desc: String,
                 val source: String,
                 val status: Status,
                 val queue: String?,
                 val time: DateTime = DateTime.now(),
                 val info: List<Pair<String, String>> = listOf())
