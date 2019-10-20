package slatekit.jobs.events

import slatekit.jobs.Job

class JobEvents : SubscribedEvents<Job>(){

    override suspend fun notify(item: Job) {
        notify(item, item.status())
    }
}