package slatekit.jobs.events

import slatekit.common.Identity
import slatekit.common.Status

/**
 * @param id: Identity of the worker
 * @param status: Status of the worker
 * @param info: Info/diagnostics provided by the worker
 */
class JobEvent(val id: Identity, val status: Status, val queue:String?)

class JobEvents : SubscribedEvents<JobEvent>() {

    override suspend fun notify(item: JobEvent) {
        notify(item, item.status)
    }
}

