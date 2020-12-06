package slatekit.jobs.support

import slatekit.core.common.Emitter
import slatekit.jobs.Event
import slatekit.jobs.Job
import slatekit.jobs.workers.Worker

class Notifier(val jobEvents: Emitter<Event.JobEvent> = Emitter<Event.JobEvent>(),
               val wrkEvents: Emitter<Event.WorkerEvent> = Emitter<Event.WorkerEvent>()) {

    suspend fun notify(job: Job) {
        val event = Event.JobEvent(job.id, job.status(), job.ctx.queue?.name)
        jobEvents.emit(event)
        jobEvents.emit(event.status.name, event)
    }

    suspend fun notify(worker: Worker<*>) {
        val event = Event.WorkerEvent(worker.id, worker.status(), worker.info())
        wrkEvents.emit(event)
        wrkEvents.emit(event.status.name, event)
    }
}
