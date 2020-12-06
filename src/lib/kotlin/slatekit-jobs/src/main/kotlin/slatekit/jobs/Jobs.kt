package slatekit.jobs

import kotlinx.coroutines.*
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes

/**
 * Registry of all the jobs and queues. This registry is used to:
 * 1. Get a job
 * 2. Perform Start | Stop | Pause | Resume | Process | Check operations on a job or worker
 * 3. Run a one-off Job
 * @param queues: List of all queues
 * @param jobs  : List of all jobs
 * @scope scope : CoroutineScope to launch jobs in
 */
class Jobs(
    val queues: List<Queue>,
    val jobs: List<Job>,
    val scope: CoroutineScope = Jobs.scope
) : Ops<Job> {

    private val lookup = jobs.map { it.id.name to it }.toMap()

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
    override operator fun get(name: String): Job? = if (lookup.containsKey(name)) lookup[name] else null

    /**
     * performs the operation on the supplied job/worker
     * 1. Job    = {Identity.area}.{Identity.service}
     * 2. Worker = {Identity.area}.{Identity.service}.{Identity.instance}
     * @param name: The name of the job/worker
     * @sample : job = "signup.emails", worker = "signup.emails.worker_1"
     */
    override suspend fun perform(name: String, action: slatekit.jobs.Action, seconds:Int?): Outcome<String> {
        // - Job     : {Identity.area}.{Identity.service}
        // - Worker  : {Identity.area}.{Identity.service}.{Identity.instance}
        // - Example : job = "signup.emails", worker = "signup.emails.worker_1"
        val parts = name.split(".")
        val jobName = "${parts[0]}.${parts[1]}"
        val workerName = if(parts.size < 3) null else "$jobName.${parts[2]}"
        val job = this[jobName] ?: return Outcomes.invalid("Unable to find job with name $jobName")

        // Start the entire job
        if (workerName == null) {
            val cmd = job.ctx.commands.job(action)
            job.request(cmd)
            return Outcomes.success("Requested $action on job $jobName")
        }

        // Start just the worker
        val worker = job.workers[workerName] ?: return Outcomes.invalid("Unable to find worker $workerName")
        val cmd = job.ctx.commands.work(worker.id, action)
        job.request(cmd)
        return Outcomes.success("Requested $action on worker $jobName/$workerName")
    }

    companion object {

        val ALL = "ALL"
        
        /**
         * Default scope used by the Jobs system
         */
        val scope: CoroutineScope by lazy {
            CoroutineScope(SupervisorJob() + Dispatchers.IO)
        }
    }
}
