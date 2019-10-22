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
import slatekit.common.queues.QueueSourceInMemory
import slatekit.cmds.Command
import slatekit.cmds.CommandRequest
import slatekit.functions.policy.Every
import slatekit.functions.policy.Limit
import slatekit.functions.policy.Ratio
import slatekit.jobs.*
import slatekit.jobs.Job.Companion.worker
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

        // Sample list of users
        val allUsers = (1..20).map { User(it, "user$it@company1.com")}

        // Sends the message ( newsletter ) to the user
        suspend fun send(msg:String, user:User) {
            // Simulate sending message to user
            runBlocking { println("Sent $msg to ${user.email}") }
        }

        // NOTE: This is a helper method used for the real example(s) below
        suspend fun sendNewsLetterBatch(offset:AtomicInteger, batchSize:Int):WorkResult {
            val start = offset.get()
            val users = if(start < 0 || start >= allUsers.size) listOf()
            else allUsers.subList(start, start + batchSize)


            // No more records so indicate done
            if(users.isEmpty())
                return WorkResult(WorkState.Done)

            // Get next page of records
            users.forEach { user -> send("New version coming out soon!", user) }

            // Update offset and totals
            offset.addAndGet(users.size)

            // Use WorkState.Next to
            // 1. Indicate that we are paging through work
            // 2. Provide a way to stop/pause/resume processing of this task in between pages
            // 3. Provides context into diagnostics/status
            return WorkResult.next(offset.get() + batchSize.toLong(), users.size.toLong(), "users")
        }


        // Option 1: Use a function for a job that runs to completion
        suspend fun sendNewsLetter():WorkResult {
            allUsers.forEach { user -> send(NEWS_LETTER_MESSAGE, user) }
            return WorkResult(WorkState.Done)
        }


        // Option 2: Use a function for a job that pages through work
        val offset1 = AtomicInteger(0)
        suspend fun sendNewsLetterWithPaging():WorkResult {
            return sendNewsLetterBatch(offset1, 4)
        }


        // Option 3: Use a function for a job that processes a task from a queue
        suspend fun sendNewsLetterFromQueue(task: Task):WorkResult {
            val userId = task.data.toInt()
            val user = allUsers.first { it.id == userId }
            send(NEWS_LETTER_MESSAGE, user)

            // Acknowledge the task or abandon task.fail()
            task.done()

            // Indicate that this can now handle more
            return WorkResult(WorkState.More)
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
                return sendNewsLetterBatch(offset, 4)
            }

            // Transition hook for when the status is changed ( e.g. from Status.Running -> Status.Paused )
            override suspend fun transition(state: Status) {
                notify("transition", listOf("status" to state.name))
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
            override fun notify(desc: String?, extra: List<Pair<String, String>>?) {
                val detail = extra?.joinToString(",") { it.first + "=" + it.second }
                // Simulate notification to email/alerts/etc
                println(desc + detail)
            }
        }
        //</doc:setup>

        //<doc:examples>
        runBlocking {
            // Sample 1: JOB that runs to completion
            val job1 = slatekit.jobs.Job(id.copy(service = "job1"), ::sendNewsLetter, scope = this)
            job1.start()   // Job dispatch
            job1.respond() // Work dispatch
            job1.respond() // Work start/finish
            println(job1.status())

            // Sample 2: JOB ( 2 Workers ) constructor with list of 2 functions which will create 2 workers
            val job2 = slatekit.jobs.Job(id.copy(service = "job2"), listOf(worker(::sendNewsLetter), worker(::sendNewsLetterWithPaging)), scope = this)
            job2.start()
            job2.respond() // Work dispatch
            job2.respond() // Work start/finish
            job2.respond() // Work start/finish

            // Sample 3: JOB ( Paged )

            val jscope = JobScope()
            val job3 = slatekit.jobs.Job(id.copy(service = "job3"), listOf(worker(::sendNewsLetterWithPaging)), scope = jscope.scope)
            //val j = jscope.perform(job3)
            //j.join()
            //Thread.sleep(10000)
            println(job3.status())

            // Sample 4: JOB ( Events ) + Subscribe to worker status changes
            val job4 = slatekit.jobs.Job(id.copy(service = "job4"), listOf(worker(::sendNewsLetterWithPaging)))
            job4.subscribe { println("Job ${it.id.name} status changed to : ${it.status().name}")}
            job4.subscribe(Status.Complete) { println("Job ${it.id.name} completed")}
            job4.workers.subscribe { it ->  println("Worker ${it.id.name}: status = ${it.status().name}")}
            job4.workers.subscribe(Status.Complete) { it ->  println("Worker ${it.id.name} completed")}
            job4.start()   // Job dispatch
            job4.respond() // Work dispatch
            job4.respond() // Work start/finish
            job4.respond() // Work start/finish
            job4.respond() // Work start/finish
            job4.respond() // Work start/finish
            job4.respond() // Work start/finish
            job4.respond() // Work start/finish
            job4.respond() // Work start/finish

            // Sample 4: JOB ( Queued ) + Subscribe to worker status changes
            val queue1 = Queue("sample_queue", Priority.Mid, QueueSourceInMemory.stringQueue(5))
            queue1.queue.send("3", mapOf("id" to "3", "name" to "newsletter", "data" to "3"))
            val job5 = slatekit.jobs.Job(id.copy(service = "job5"), listOf(::sendNewsLetterFromQueue), queue1)
            job5.start()   // Job dispatch
            job5.respond() // Work dispatch
            job5.respond() // Work start/finish
            job5.respond() // Work start/finish
            println(job5.status())

            // Sample 5: JOB ( Policies ) with policies to add behavior / strategies to worker, this adds:
            // 1. a callback for every 10 items processed
            // 2. a limit to processing at most 12 items ( to support running a job in "waves" )
            // 3. a threshold / error limit of .1 ( 10% )
            val job6 = slatekit.jobs.Job(id.copy(service = "job6"), listOf(worker(::sendNewsLetterWithPaging)))
            job6.workers.policy(Every(10) { req, res -> println(req.task.id + ":" + res.msg) })
            job6.workers.policy(Limit(12) { req -> req.context.stats.counts } )
            job6.workers.policy(Ratio(.1, slatekit.results.Status.Errored(0, "")) { req -> req.context.stats.counts } )
            job6.start()

            // Sample 7: JOB ( Worker ) implementation with queue
            val queue2 = Queue("sample_queue", Priority.Mid, QueueSourceInMemory.stringQueue(5))
            val job7 = slatekit.jobs.Job(id.copy(service = "job7"), listOf(NewsLetterWorker()), queue2)
            job7.start()

            // Kick off the jobs by
            //job3.respond()
            job4.respond()
            job5.respond()
            job6.respond()

            // Delay for 30 seconds
            delay(30000)
        }
        //</doc:examples>
        return Success("")
    }
}