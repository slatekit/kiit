package slatekit.samples.job

import kotlinx.coroutines.runBlocking
import slatekit.actors.*
import slatekit.common.Agent
import slatekit.common.Identity
import slatekit.common.SimpleIdentity
import slatekit.common.args.Args
import slatekit.core.queues.AsyncQueue
import slatekit.core.queues.InMemoryQueue
import slatekit.connectors.jobs.JobQueue
import slatekit.jobs.Manager
import slatekit.jobs.Jobs
import slatekit.jobs.Middleware
import slatekit.jobs.Priority
import slatekit.jobs.support.Events
import slatekit.results.Failure
import slatekit.results.Success


/**
 * NOTES:
 * 1. You can use the Slate Kit Application template to
 * get support for command line args, environment selection, confs, life-cycle methods and help usage
 * @see https://www.slatekit.com/arch/app/
 *
 * RUN OPTIONS:
 *
 * 1. One Time Job: gradle run --args='-sample=single'
 * 2. Paged    Job: gradle run --args='-sample=paging'
 * 3. Queued   Job: gradle run --args='-sample=queued'
 * 4. Worker   Job: gradle run --args='-sample=worker'
 */
fun run(raw: Array<String>) {
    when (val parsed = Args.parseArgs(raw)) {
        is Failure -> println(parsed.error.message)
        is Success -> {
            // Args
            val args = parsed.value

            // Sample to run
            val sample = args.getStringOrNull("sample")
            println("starting sample job: $sample")

            // Run sample
            runBlocking {
                when (sample?.toLowerCase()) {
                    "onetime" -> executeOneTime()
                    "paging" -> executePaged()
                    "queued" -> executeQueued()
                    "worker" -> executeWorker()
                    else -> println("Unknown sample provided : $sample")
                }
            }
        }
    }
}


/**
 * Executes a 1 time job
 */
suspend fun executeOneTime() {
    // Create manager with worker as a lambda
    val mgr = Manager(id = Identity.job("samples", "newsletter"), op = ::sendNewsLetter)

    // Starts all workers
    mgr.start()

    // Kick of the management
    mgr.work().join()
}


suspend fun executePaged() {
    // Create manager with worker as a lambda
    val mgr = Manager(id = Identity.job("samples", "newsletter"), op = ::sendNewsLetterWithPaging)

    // Start by issuing commands
    mgr.start()

    // Kick of the work
    mgr.work().join()
}


suspend fun executeQueued() {
    // Identity of job/queue
    val id = Identity.job("samples", "newsletter")

    // Create sample queue ( in-memory )
    val queue1 = JobQueue("queue-1", Priority.Mid, AsyncQueue.of(InMemoryQueue.stringQueue(5)), id = id)
    addTasks(queue1, 5)

    // Create manager with worker as a lambda
    val mgr = Manager(id = id, op = ::sendNewsLetterFromQueue, queue = queue1)

    // Start by issuing commands
    mgr.start()

    // Kick of the work
    mgr.work().join()
}


suspend fun executeWorker() {
    // Identity of job/queue
    val id = Identity.job("samples", "newsletter")

    // Create sample queue ( in-memory )
    val queue1 = JobQueue("queue-1", Priority.Mid, AsyncQueue.of(InMemoryQueue.stringQueue(5)), id = id)
    addTasks(queue1, 5)

    // Create manager with worker as a lambda
    val mgr = Manager(id = Identity.job("samples", "newsletter"), worker = NewsLetterWorker(id), queue = queue1)

    // Start by issuing commands
    mgr.start()

    // Kick of the work
    mgr.work().join()
}


suspend fun addTasks(queue:JobQueue, count:Int) {
    (1..count).forEach { num ->
        queue.send("user=${num}", mapOf(
                "id" to "task_$num",
                "name" to "sendNewsLetter",
                "xid" to "xid_$num",
                "tag" to "sample_tag_$num"
        ))
    }
}