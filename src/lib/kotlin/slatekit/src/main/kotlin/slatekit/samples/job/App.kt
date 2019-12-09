package slatekit.samples.job

import kotlinx.coroutines.runBlocking
import slatekit.app.App
import slatekit.app.AppOptions
import slatekit.common.Agent
import slatekit.common.Context
import slatekit.common.SimpleIdentity
import slatekit.common.args.ArgsSchema
import slatekit.common.utils.B64Java8
import slatekit.common.encrypt.Encryptor
import slatekit.common.info.About
import slatekit.core.queues.AsyncQueue
import slatekit.core.queues.InMemoryQueue
import slatekit.jobs.Job
import slatekit.jobs.Jobs
import slatekit.jobs.Priority
import slatekit.jobs.Queue
import slatekit.results.Failure
import slatekit.results.Success
import slatekit.results.Try


/**
 * Slate Kit Application template
 * This provides support for command line args, environment selection, confs, life-cycle methods and help usage
 * @see https://www.slatekit.com/arch/app/
 */
class App(ctx: Context) : App<Context>(ctx, AppOptions(printSummaryBeforeExec = true)) {

    companion object {

        // setup the command line arguments.
        // NOTE:
        // 1. These values can can be setup in the env.conf file
        // 2. If supplied on command line, they override the values in .conf file
        // 3. If any of these are required and not supplied, then an error is display and program exists
        // 4. Help text can be easily built from this schema.
        val schema = ArgsSchema()
                .text("","job.name", "name of job to run", false, "paging", "paging", "single | paging | queued | worker")
                .text("","env", "the environment to run in", false, "dev", "dev", "dev1|qa1|stg1|pro")
                .text("","log.level", "the log level for logging", false, "info", "info", "debug|info|warn|error")


        /**
         * Default static info about the app.
         * This can be overriden in your env.conf file
         */
        val about = About(
                area = "samples",
                name = "myjob.name",
                desc = "myjob.desc",
                company = "myapp.company",
                region = "",
                version = "1.0.0",
                url = "myapp.url",
                contact = "",
                tags = "job",
                examples = ""
        )

        /**
         * Encryption support
         */
        val encryptor = Encryptor("aksf2409bklja24b", "k3l4lkdfaoi97042", B64Java8)
    }


    override suspend fun init(): Try<Boolean> {
        println("initializing")
        return super.init()
    }


    override suspend fun exec(): Try<Any> {
        println("executing")
        runBlocking {

            // Identity for the jobs
            val id = SimpleIdentity("samples", "newsletter", Agent.Job, "dev")

            // Sample Queues: In-Memory
            val queue1 = Queue("queue1", Priority.Mid, AsyncQueue.of(InMemoryQueue.stringQueue(5)))
            val queue2 = Queue("queue2", Priority.Mid, AsyncQueue.of(InMemoryQueue.stringQueue(5)))

            // Sample Data: for queue
            (1 .. 5).forEach {
                queue1.queue.send(it.toString(), mapOf(
                        "id"   to "sample_id_$it",
                        "name" to "sendNewsLetter",
                        "xid"  to "sample_correlation_id_$it",
                        "tag"  to "sample_tag_$it"
                ))
                queue2.queue.send(it.toString(), mapOf(
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
                            Job(id.copy(service = "single"), ::sendNewsLetter),
                            Job(id.copy(service = "paging"), listOf(::sendNewsLetterWithPaging)),
                            Job(id.copy(service = "queued"), listOf(::sendNewsLetterFromQueue), queue1),
                            Job(id.copy(service = "worker"), listOf(NewsLetterWorker(id)), queue2)
                    )
            )

            // For Sample purposes, choose which one to run e.g. "samples.single | samples.paging | samples.queued | samples.worker"
            val name = ctx.args.getStringOrElse("job.name", "paging")
            val fullName = "samples.$name"

            // Run
            val j = jobs.run(fullName)
            when(j) {
                is Success -> j.value.join()
                is Failure -> {
                    println("\nERROR : =====================")
                    println(j.error.msg)
                    println("\n")
                }
            }
        }
        return Success(true)
    }


    override suspend fun end(): Try<Boolean> {
        println("ending")
        return super.end()
    }
}