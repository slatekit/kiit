package slatekit.jobs.events

import slatekit.common.Identity
import slatekit.common.Status

/**
 * @param id: Identity of the worker
 * @param status: Status of the worker
 * @param info: Info/diagnostics provided by the worker
 */
class WorkerEvent(val id:Identity, val status: Status, val info:List<Pair<String, String>>)


class WorkerEvents : SubscribedEvents<WorkerEvent>() {

    override suspend fun notify(item: WorkerEvent) {
        notify(item, item.status)
    }
}
