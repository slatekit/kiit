package kiit.jobs

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kiit.actors.Message
import kiit.common.Identity
import kiit.common.log.Logger
import kiit.common.log.LoggerConsole
import kiit.jobs.support.DefaultScheduler
import kiit.jobs.support.Backoffs
import kiit.jobs.support.Scheduler
import kiit.jobs.support.Notifier

/**
 * Represents all the dependencies needed for a Job
 * @param id        : Identity of the job e.g. signup.alerts.job.qat.ABC123 @see[kiit.common.Identity]
 * @param channel   : Channel to communicate commands to a job and between job/workers
 * @param workers   : List of all @see[Worker] involved in this job
 * @param logger    : Logger used inside the job
 * @param backoffs  : Exponential backoff times during idling or pauses
 * @param notifier  : Notifier to emit job/work events to subscribers
 * @param scheduler : Scheduler used to continue resuming work
 */
data class Context(val id: Identity,
                   val workers: List<Worker<*>>,
                   val mode   : Int = Channel.UNLIMITED,
                   val channel: Channel<Message<Task>> = Channel(capacity = 10),
                   val logger : Logger = LoggerConsole(),
                   val queue  : Queue? = null,
                   val scope  : CoroutineScope = Jobs.scope,
                   val middleware: Middleware? = null,
                   val backoffs: Backoffs = Backoffs(Backoffs.times()),
                   val notifier: Notifier = Notifier(),
                   val scheduler: Scheduler = DefaultScheduler()
)
