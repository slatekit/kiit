package slatekit.jobs.events

import slatekit.jobs.*

class WorkerEvents(val workers: Workers) : SubscribedEvents<Worker<*>>() {

    override suspend fun notify(item: Worker<*>) {
        notify(item, item.status())
    }
}