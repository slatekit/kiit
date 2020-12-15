package slatekit.samples.job

import kotlinx.coroutines.runBlocking
import slatekit.common.Agent
import slatekit.common.SimpleIdentity
import slatekit.common.args.Args
import slatekit.core.queues.AsyncQueue
import slatekit.core.queues.InMemoryQueue
import slatekit.integration.jobs.JobQueue
import slatekit.jobs.Manager
import slatekit.jobs.Jobs
import slatekit.jobs.Priority
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
 * 1. One Time Job: gradle run --args='-job.name=single'
 * 2. Paged    Job: gradle run --args='-job.name=paging'
 * 3. Queued   Job: gradle run --args='-job.name=queued'
 * 4. Worker   Job: gradle run --args='-job.name=worker'
 */
fun main(raw: Array<String>){
    println("Args: =====================")
    raw.forEachIndexed { ndx, value ->  println("$ndx : $value") }
    println("\n")

    val parsed = Args.parseArgs(raw)
    when(parsed){
        is Failure ->  println(parsed.error.message)
        is Success ->  {
            val args = parsed.value
            println("job.name: " + args.getStringOrElse("job.name", "nothing"))
            args.named.forEach { pair -> println("${pair.key}:${pair.value}")}
            run(args)
        }
    }
}


fun run(args: Args){
    runBlocking {

        // Identity for the jobs
        val id = SimpleIdentity("samples", "newsletter", Agent.Job, "dev")

        // Sample Queues: In-Memory
        val queue1 = JobQueue("q1", Priority.Mid, AsyncQueue.of(InMemoryQueue.stringQueue(5)))
        val queue2 = JobQueue("q2", Priority.Mid, AsyncQueue.of(InMemoryQueue.stringQueue(5)))

        // Sample Data: for queue
        (1 .. 5).forEach {
            queue1.send(it.toString(), mapOf(
                    "id"   to "sample_id_$it",
                    "name" to "sendNewsLetter",
                    "xid"  to "sample_correlation_id_$it",
                    "tag"  to "sample_tag_$it"
            ))
            queue2.send(it.toString(), mapOf(
                    "id"   to "sample_id_$it",
                    "name" to "sendNewsLetter",
                    "xid"  to "sample_correlation_id_$it",
                    "tag"  to "sample_tag_$it"
            ))
        }

        // Job Registry
        val jobs = Jobs(
                listOf(queue1, queue2),
                listOf(
                        Manager(id.copy(service = "single"), ::sendNewsLetter),
                        Manager(id.copy(service = "paging"), listOf(::sendNewsLetterWithPaging)),
                        Manager(id.copy(service = "queued"), listOf(::sendNewsLetterFromQueue), queue1),
                        Manager(id.copy(service = "worker"), NewsLetterWorker(id.copy(tags = listOf("worker"))), queue2)
                )
        )

        // For Sample purposes, choose which one to run e.g. "samples.single | samples.paging | samples.queued | samples.worker"
        val name = args.getStringOrElse("job.name", "paging")
        val fullName = "samples.$name"

        // Run
        val result = jobs.start(fullName)
        when(result.success) {
            true -> result.msg
            false -> {
                println("\nERROR : =====================")
                println(result.msg)
                println("\n")
            }
        }
    }
}