package slatekit.jobs.events

import slatekit.jobs.workers.Worker
import slatekit.jobs.workers.Workers

class WorkerEvents(val workers: Workers) : SubscribedEvents<Worker<*>>() {

    override suspend fun notify(item: Worker<*>) {
        notify(item, item.status())
    }
}
