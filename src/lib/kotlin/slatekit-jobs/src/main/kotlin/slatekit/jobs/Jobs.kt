package slatekit.jobs

import kotlinx.coroutines.*
import slatekit.actors.Action
import slatekit.actors.Controls
import slatekit.actors.Feedback

/**
 * Registry of all the jobs and queues. This registry is used to:
 * 1. Get a job
 * 2. Perform Start | Stop | Pause | Resume | Process | Check operations on a job or worker
 * 3. Run a one-off Job
 * @param queues: List of all queues
 * @param jobs  : List of all jobs
 * @scope scope : CoroutineScope to launch jobs in
 */
class Jobs(val queues: List<Queue>,
           val jobs: List<Job>,
           val scope: CoroutineScope = Jobs.scope) : Controls {

    private val jobNames = jobs.map { it.jctx.id.name to it }.toMap()
    private val wrkInsts = jobs.map { it.workers.contexts.map { wctx -> wctx.id.instance to Pair(it, wctx) } }.flatten().toMap()

    /**
     * Ids of all the jobs
     */
    val ids = jobs.map { it.id }

    /**
     * Number of jobs
     */
    fun size(): Int = jobs.size

    /**
     * Gets the job by name e.g. "area.service"
     */
    fun get(name: String): Job? = if (jobNames.containsKey(name)) jobNames[name] else null


    /**
     * @param target : Job = "signup.email", Worker = "signup.email.worker_123"
     */
    override suspend fun control(action: Action, msg: String?, target: String): Feedback {
        val job = jobNames[target]
        val wrk = wrkInsts[target]
        return when {
            job != null -> {
                job.control(action, null, job.id)
                Feedback(true, "")
            }
            wrk != null -> {
                val j = wrk.first
                val w = wrk.second
                j.control(action, null, target = w.id.id)
                Feedback(true, "")
            }
            else -> Feedback(false, "Unable to find job or worker $target")
        }
    }

    companion object {

        const val ALL = "ALL"

        /**
         * Default scope used by the Jobs system
         */
        val scope: CoroutineScope by lazy {
            CoroutineScope(SupervisorJob() + Dispatchers.IO)
        }
    }
}
