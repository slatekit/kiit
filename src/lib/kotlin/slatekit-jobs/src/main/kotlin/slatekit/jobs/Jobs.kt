package slatekit.jobs

import kotlinx.coroutines.*
import slatekit.common.Identity
import slatekit.jobs.support.Ops
import slatekit.jobs.support.Command
import slatekit.jobs.support.Utils
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
     * Sends a command to take action ( start, pause, stop, etc ) on all items ( jobs )
     */
    override suspend fun send(action: Action):Outcome<String> {
        val results = ids.mapNotNull { id ->
            // "{identity.area}.{identity.service}"
            // e.g. "signup.emails"
            val job = this[id.name]
            job?.let { send(job.ctx.commands.job(id, action)) }
        }
        return when(results.all { it.success }){
            true  -> Outcomes.success("Sent command=${action.name}, type=job, target=all")
            false -> results.first { !it.success }
        }
    }

    /**
     * Sends a command to take action ( start, pause, stop, etc )  on a specific job or worker by id
     */
    override suspend fun send(id: Identity, action: Action, note: String):Outcome<String> {
        // "{identity.area}.{identity.service}"
        // e.g. "signup.emails"
        return when(val job = this[id.name]){
            null -> Outcomes.invalid("Unable to find job with name ${id.name}")
            else -> {
                when(Utils.isWorker(id)){
                    false -> send(job.ctx.commands.job(id, action))
                    true  -> send(job.ctx.commands.work(id, action))
                }
            }
        }
    }

    /**
     * Sends a command to take action ( start, pause, stop, etc ) on a specific job or worker
     */
    override suspend fun send(command: Command):Outcome<String> {
        // "{identity.area}.{identity.service}"
        // e.g. "signup.emails"
        return when(val job = this[command.identity.name]) {
            null -> Outcomes.invalid("Unable to find job with name ${command.identity.name}")
            else -> job.send(command)
        }
    }

    /**
     * Converts the name supplied to either the identity of either
     * 1. Job    : {identity.area}.{identity.service}                       e.g. "signup.emails"
     * 2. Worker : {identity.area}.{identity.service}.{identity.instance}   e.g. "signup.emails.worker_1"
     */
    override fun toId(name: String): Identity? {
        if(name.isBlank()) return null
        val parts = name.split(".")
        return when (parts.size) {
            2 -> this["${parts[0]}.${parts[1]}"]?.id
            3 -> this["${parts[0]}.${parts[1]}"]?.workers?.getIds()?.first { it.instance == name }
            else -> null
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
