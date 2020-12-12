package slatekit.jobs

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import slatekit.actors.Message
import slatekit.common.Identity
import slatekit.common.log.Logger
import slatekit.common.log.LoggerConsole
import slatekit.core.common.Backoffs
import slatekit.core.common.DefaultScheduler
import slatekit.core.common.Scheduler
import slatekit.jobs.support.Notifier
import slatekit.policy.Policy

/**
 * Represents all the dependencies needed for a Job
 * @param id        : Identity of the job e.g. signup.alerts.job.qat.ABC123 @see[slatekit.common.Identity]
 * @param channel   : Channel to communicate commands to a job and between job/workers
 * @param workers   : List of all @see[Worker] involved in this job
 * @param logger    : Logger used inside the job
 * @param policies  : Policies that control or respond to work/task execution
 * @param backoffs  : Exponential backoff times during idling or pauses
 * @param notifier  : Notifier to emit job/work events to subscribers
 * @param scheduler : Scheduler used to continue resuming work
 */
data class Context(val id: Identity,
                   val channel: Channel<Message<Task>>,
                   val workers: List<Worker<*>>,
                   val logger : Logger = LoggerConsole(),
                   val queue  : Queue? = null,
                   val scope  : CoroutineScope = Jobs.scope,
                   val policies: List<Policy<WorkRequest, WResult>> = listOf(),
                   val backoffs: Backoffs = Backoffs(Backoffs.times()),
                   val notifier: Notifier = Notifier(),
                   val scheduler: Scheduler = DefaultScheduler()
)
