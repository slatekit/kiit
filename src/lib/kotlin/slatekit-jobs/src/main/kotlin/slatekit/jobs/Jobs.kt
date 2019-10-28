package slatekit.jobs

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes

/**
 * Simple registry of all the jobs
 * @param queues: List of all queues
 * @param jobs: List of all jobs
 * @scope scope: CoroutineScope to launch jobs in
 */
class Jobs(
    val queues: List<Queue>,
    val jobs: List<slatekit.jobs.Job>,
    val scope: CoroutineScope = Jobs.scope
) {
    private val lookup = jobs.map { it.id.name to it }.toMap()

    /**
     * Ids of all the jobs
     */
    val ids = jobs.map { it.id }

    /**
     * Gets the job by name e.g. "area.service"
     */
    operator fun get(name: String): Job? = if (lookup.containsKey(name)) lookup[name] else null

    /**
     * Number of jobs
     */
    fun size(): Int = jobs.size

    /**
     * Runs the jobs by starting it and then managing it ( listening of requests )
     */
    suspend fun start(name: String): Outcome<String> {
        return perform(name) { job ->
            job.start()
            "Job $name started"
        }
    }

    /**
     * Runs the jobs by starting it and then managing it ( listening of requests )
     */
    suspend fun run(name: String): Outcome<kotlinx.coroutines.Job> {
        return perform(name) { job ->
            run(job)
        }
    }

    /**
     * Responds to the requests in the jobs request queue
     * This is intended for on-demand / forced running of a request/job
     * rather than kicking of the management of a job ( continously listening of requests )
     */
    suspend fun respond(name: String, count: Int, start: Boolean = false): Outcome<String> {
        return perform(name) { job ->
            if (start) {
                job.start()
            }
            (0 until count).forEach {
                job.respond()
            }
            "Responded $count  times"
        }
    }

    /**
     * Runs the jobs by starting it and then managing it ( by
     * continuously listening of requests )
     */
    suspend fun run(job: slatekit.jobs.Job): kotlinx.coroutines.Job {
        val j = scope.launch {
            job.start()
            job.manage()
        }
        return j
    }

    private suspend fun <T> perform(name: String, op: suspend(Job) -> T): Outcome<T> {
        val job = this[name]
        val result = when (job) {
            null -> Outcomes.invalid("Job with name $name not found")
            else -> Outcomes.success(op(job))
        }
        return result
    }

    companion object {

        /**
         * Default scope used by the Jobs system
         */
        val scope: CoroutineScope by lazy {
            CoroutineScope(SupervisorJob() + Dispatchers.IO)
        }
    }
}
