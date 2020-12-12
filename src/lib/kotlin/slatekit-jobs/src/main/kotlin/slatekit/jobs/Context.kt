package slatekit.jobs

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import slatekit.actors.Message
import slatekit.actors.WResult
import slatekit.common.Identity
import slatekit.common.log.Logger
import slatekit.common.log.LoggerConsole
import slatekit.core.common.Backoffs
import slatekit.core.common.DefaultScheduler
import slatekit.core.common.Scheduler
import slatekit.jobs.support.Notifier
import slatekit.jobs.WorkRequest
import slatekit.jobs.Worker
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
) {
    companion object {
        /**
         * Initialize with just a function that will handle the work
         * @sample
         *  val job1 = Job(Identity.job("signup", "email"), ::sendEmail)
         *  val job2 = Job(Identity.job("signup", "email"), suspend {
         *      // do work here
         *      WResult.Done
         *  })
         */
        operator fun invoke(id: Identity, op: suspend () -> WResult, scope: CoroutineScope = Jobs.scope): Context {
            return Context(id, listOf(Job.worker(op)), null, scope, listOf())
        }

        /**
         * Initialize with just a function that will handle the work
         * @sample
         *  val job1 = Job(Identity.job("signup", "email"), ::sendEmail)
         *  val job2 = Job(Identity.job("signup", "email"), suspend { task ->
         *      println("task id=${task.id}")
         *      // do work here
         *      WResult.Done
         *  })
         */
        operator fun invoke(id: Identity, op: suspend (Task) -> WResult, queue: Queue? = null, scope: CoroutineScope = Jobs.scope, policies: List<Policy<WorkRequest, WResult>> = listOf()): Context {
            return Context(id, listOf(op), queue, scope, policies)
        }

        /**
         * Initialize with a list of functions to excecute work
         */
        operator fun invoke(id: Identity, ops: List<suspend (Task) -> WResult>, queue: Queue? = null, scope: CoroutineScope = Jobs.scope, policies: List<Policy<WorkRequest, WResult>> = listOf()): Context {
            return Context(id, Job.coordinator(), Job.workers(id, ops), queue = queue, scope = scope, policies = policies)
        }

        /**
         * Initialize with just a function that will handle the work
         *  val id = Identity.job("signup", "email")
         *  val job1 = Job(id, EmailWorker(id.copy(tags = listOf("worker")))
         */
        operator fun invoke(id: Identity, worker: Worker<*>, queue: Queue? = null, scope: CoroutineScope = Jobs.scope,
                            policies: List<Policy<WorkRequest, WResult>> = listOf()): Context {
            return Context(id, Job.coordinator(), listOf(worker), queue = queue, scope = scope, policies = policies)
        }
    }
}
