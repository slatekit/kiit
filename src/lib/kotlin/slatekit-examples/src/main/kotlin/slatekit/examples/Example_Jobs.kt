/**
<slate_header>
author: Kishore Reddy
url: www.github.com/code-helix/slatekit
copyright: 2015 Kishore Reddy
license: www.github.com/code-helix/slatekit/blob/master/LICENSE.md
desc: A tool-kit, utility library and server-backend
usage: Please refer to license on github for more info.
</slate_header>
 */


package slatekit.examples

//<doc:import_required>
import kotlinx.coroutines.*
import slatekit.common.*
//</doc:import_required>

//<doc:import_examples>
import slatekit.core.queues.InMemoryQueue
import slatekit.cmds.Command
import slatekit.cmds.CommandRequest
import slatekit.common.log.LoggerConsole
import slatekit.core.queues.AsyncQueue
import slatekit.policy.policies.Every
import slatekit.policy.policies.Limit
import slatekit.policy.policies.Ratio
import slatekit.jobs.*
import slatekit.jobs.workers.WorkResult
import slatekit.jobs.workers.Worker
import slatekit.results.Try
import slatekit.results.Success
import java.util.concurrent.atomic.AtomicInteger

//</doc:import_examples>


class Example_Jobs : Command("utils"), CoroutineScope by MainScope() {

    override fun execute(request: CommandRequest): Try<Any> {
        //<doc:setup>

        // Sample User model
        data class User(val id:Int, val email:String)

        val NEWS_LETTER_MESSAGE = "New version coming out soon!"

        // Sample list of 20 users
        val allUsers = (1..20).map { User(it, "user$it@company1.com")}

        // Sends the message ( newsletter ) to the user
        suspend fun send(sender:String, msg:String, user:User) {
            // Simulate sending message to user
            runBlocking { println("Source: ${sender}: Sent $msg to ${user.email}") }
        }

        // NOTE: This is a helper method used for the real example(s) below
        suspend fun sendNewsLetterBatch(sender:String, offset:AtomicInteger, batchSize:Int): WorkResult {
            val start = offset.get()
            val users = if(start < 0 || start >= allUsers.size) listOf()
            else allUsers.subList(start, start + batchSize)


            // No more records so indicate done
            if(users.isEmpty())
                return WorkResult.Done

            // Get next page of records
            users.forEach { user -> send(sender,"New version coming out soon!", user) }

            // Update offset and totals
            offset.addAndGet(users.size)

            // Use WorkState.Next to
            // 1. Indicate that we are paging through work
            // 2. Provide a way to stop/pause/resume processing of this task in between pages
            // 3. Provides context into diagnostics/status
            return WorkResult.next(offset.get() + batchSize.toLong(), users.size.toLong(), "users")
        }


        // Option 1: Use a function for a job that runs to completion
        suspend fun sendNewsLetter(task: Task): WorkResult {
            allUsers.forEach { user -> send(task.job, NEWS_LETTER_MESSAGE, user) }
            return WorkResult.Done
        }


        // Option 2: Use a function for a job that pages through work
        val offset1 = AtomicInteger(0)
        suspend fun sendNewsLetterWithPaging(task: Task): WorkResult {
            println(task.id)    // abc123
            println(task.from)  // queue://notification
            println(task.job)   // job1
            println(task.name)  // users.sendNewsletter
            println(task.data)  // { ... } json payload
            println(task.xid)   // 12345   correlation id

            // WorkResult.next(offset.get() + batchSize.toLong(), users.size.toLong(), "users")
            return sendNewsLetterBatch(task.job, offset1, 4)
        }


        // Option 3: Use a function for a job that processes a task from a queue
        suspend fun sendNewsLetterFromQueue(task: Task): WorkResult {
            val userId = task.data.toInt()
            val user = allUsers.first { it.id == userId }
            send(task.job, NEWS_LETTER_MESSAGE, user)

            // Acknowledge the task or abandon task.fail()
            task.done()

            // Indicate that this can now handle more
            return WorkResult.More
        }


        // Option 4: Extend from a worker to have more control over the life-cycle
        val id = SimpleIdentity("samples", "newsletter", Agent.Job, "dev")
        class NewsLetterWorker : Worker<String>(id) {
            private val offset = AtomicInteger(0)

            // Initialization hook ( for setup / logs / alerts )
            override suspend fun init() {
                notify("initializing", listOf(("id" to this.id.name)))
            }

            // Implement your work here.
            // NOTE: If you are not using a queue, this task will be empty e.g. Task.empty
            override suspend fun work(task:Task): WorkResult {
                return sendNewsLetterBatch(task.job, offset, 4)
            }

            // Transition hook for when the status is changed ( e.g. from Status.Running -> Status.Paused )
            override suspend fun move(state: Status, note:String?, sendNotification:Boolean) {
                if(sendNotification) {
                    notify("move", listOf("status" to state.name))
                }
            }

            // Completion hook ( for logic / logs / alerts )
            override suspend fun done() {
                notify("done", listOf(("id" to this.id.name)))
            }

            // Failure hook ( for logic / logs / alerts )
            override suspend fun fail(err:Throwable?) {
                notify("failure", listOf(("id" to this.id.name), ("err" to (err?.message ?: ""))))
            }

            // Initialization hook ( for setup / logs / alerts )
            override suspend fun notify(desc: String?, extra: List<Pair<String, String>>?) {
                val detail = extra?.joinToString(",") { it.first + "=" + it.second }
                // Simulate notification to email/alerts/etc
                println(desc + detail)
            }
        }
        //</doc:setup>

        //<doc:examples>
        runBlocking {

            // Sample Queues
            val queue1 = slatekit.jobs.Queue("queue1", Priority.Mid, AsyncQueue.of(InMemoryQueue.stringQueue(5)))
            val queue2 = slatekit.jobs.Queue("queue2", Priority.Mid, AsyncQueue.of(InMemoryQueue.stringQueue(5)))

            // Registry
            val logger = LoggerConsole()
            val jobs = Jobs(
                    listOf(queue1, queue2),
                    listOf(
                            slatekit.jobs.Job(id.copy(service = "job1"), ::sendNewsLetter),
                            slatekit.jobs.Job(id.copy(service = "job2"), listOf(::sendNewsLetter, ::sendNewsLetterWithPaging)),
                            slatekit.jobs.Job(id.copy(service = "job3"), listOf(::sendNewsLetterWithPaging)),
                            slatekit.jobs.Job(id.copy(service = "job4"), listOf(::sendNewsLetterWithPaging)),
                            slatekit.jobs.Job(id.copy(service = "job5"), listOf(::sendNewsLetterFromQueue), queue1),
                            slatekit.jobs.Job(id.copy(service = "job6"), listOf(NewsLetterWorker()), queue2),

                            slatekit.jobs.Job(id.copy(service = "job7"), listOf(::sendNewsLetterWithPaging), policies = listOf(
                                    Every(10, { req, res -> println("Paged : " + req.task.id + ":" + res.msg) }),
                                    Limit(12, true, { req -> req.context.stats.counts }),
                                    Ratio(.1, slatekit.results.Failed.Errored(0, ""), { req -> req.context.stats.counts })
                                )
                            )
                    )
            )

            //jobs.run("samples.job7")
            val job = jobs.get("samples.job7")
            job?.let { job ->

                // Ids
                println("id   : " + job.id.id)
                println("name : " + job.id.name)
                println("full : " + job.id.fullname)
                println("area : " + job.id.area)
                println("svc  : " + job.id.service)
                println("agent: " + job.id.agent.name)
                println("env  : " + job.id.env)
                println("inst : " + job.id.instance)
                println("tags : " + job.id.tags)

//                // Actions
//                job.start()
//                job.stop()
//                job.pause()
//                job.resume()
//                job.process()
//
//                // Status: slatekit.common.Status
//                val status = job.status()
//                println(status.name)
//
//                // Checks
//                job.isIdle()
//                job.isRunning()
//                job.isPaused()
//                job.isStopped()
//                job.isComplete()
//                job.isFailed()
//                job.isStoppedOrPaused()
//                job.isState(slatekit.common.Status.Starting)
//
//                // Subscribe to changes in status
//                job.subscribe { println("Job: ${it.id} status changed to ${it.status().name}") }
//
//                // Subscribe to change to Stopped status
//                job.subscribe(Status.Stopped) { println("Job: ${it.id} stopped!")  }

                // Get the workers collection
                val workers = job.workers

                // Get all worker ids
                val workerIds = workers.getIds()

                // Get the context for a specific worker
                val workerContext = job.workers.get(workerIds.first())

                // The worker performing work on tasks
                workerContext?.let { ctx ->

                    // Identity of the worker parent ( Job.id )
                    println(ctx.id)

                    // Worker itself
                    ctx.worker

                    // Worker middleware applied
                    ctx.policies

                    // Worker statistics
                    // Calls: Simple counters to count calls to a worker
                    println("calls.totalRuns: " + ctx.stats.calls.totalRuns())
                    println("calls.totalPassed: " + ctx.stats.calls.totalPassed())
                    println("calls.totalFailed: " + ctx.stats.calls.totalFailed())

                    // Counts: Counts the result success/failure categories
                    // See slatekit.results.Status.kt for more info
                    println("counts.totalProcessed : " + ctx.stats.counts.totalProcessed())
                    println("counts.totalSucceeded : " + ctx.stats.counts.totalSucceeded())
                    println("counts.totalDenied    : " + ctx.stats.counts.totalDenied())
                    println("counts.totalInvalid   : " + ctx.stats.counts.totalInvalid())
                    println("counts.totalIgnored   : " + ctx.stats.counts.totalIgnored())
                    println("counts.totalErrored   : " + ctx.stats.counts.totalErrored())
                    println("counts.totalUnexpected: " + ctx.stats.counts.totalUnexpected())

                    // Lasts: Stores the last request/result of an call to work
                    println("lasts.totalProcessed : " + ctx.stats.lasts?.lastProcessed())
                    println("lasts.totalSucceeded : " + ctx.stats.lasts?.lastSuccess())
                    println("lasts.totalDenied    : " + ctx.stats.lasts?.lastDenied())
                    println("lasts.totalInvalid   : " + ctx.stats.lasts?.lastInvalid())
                    println("lasts.totalIgnored   : " + ctx.stats.lasts?.lastIgnored())
                    println("lasts.totalErrored   : " + ctx.stats.lasts?.lastErrored())
                    println("lasts.totalUnexpected: " + ctx.stats.lasts?.lastUnexpected())

                    // You can also hook up loggers and events
                    ctx.stats.logger
                    ctx.stats.events

                    println(id.id)
                }


                println("")
                ""
            }
            //jobs.run("samples.job2")
            delay(50000)
        }
        //</doc:examples>
        return Success("")
    }


    private suspend fun runSamples(jobs:Jobs){

        // Sample 1: JOB that runs to completion
        jobs.respond("samples.job1", 2, start = true)

        // Sample 2: JOB ( 2 Workers ) constructor with list of 2 functions which will create 2 workers
        jobs.respond("samples.job2", 3, start = true)

        // Sample 3: JOB ( Paged )
        jobs.run("samples.job3")

        // Sample 4: JOB ( Events ) + Subscribe to worker status changes
        jobs["samples.job4"]?.let { job ->
            job.subscribe { println("Job ${it.id.name} status changed to : ${it.status.name}") }
            job.subscribe(Status.Complete) { println("Job ${it.id.name} completed") }
            job.workers.subscribe { it -> println("Worker ${it.id.name}: status = ${it.status.name}") }
            job.workers.subscribe(Status.Complete) { it -> println("Worker ${it.id.name} completed") }
        }
        jobs.respond("samples.job4", 7, start = true)

        // Sample 5: JOB ( Queued ) + Subscribe to worker status changes
        jobs.respond("samples.job5", 3, start = true)

        // Sample 6: JOB ( Worker ) implementation with queue
        jobs.start("samples.job6")

        // Sample 7: JOB ( Policies ) with policies to add behavior / strategies to worker, this adds:
        // 1. a callback for every 10 items processed
        // 2. a limit to processing at most 12 items ( to support running a job in "waves" )
        // 3. a threshold / error limit of .1 ( 10% )
        jobs.respond("samples.job7", 4)
    }
}