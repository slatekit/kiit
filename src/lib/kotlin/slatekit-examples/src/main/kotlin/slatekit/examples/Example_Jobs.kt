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
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import slatekit.common.*
//</doc:import_required>

//<doc:import_examples>
import slatekit.common.queues.QueueSourceInMemory
import slatekit.cmds.Command
import slatekit.cmds.CommandRequest
import slatekit.jobs.*
import slatekit.results.Try
import slatekit.results.Success
import java.util.concurrent.atomic.AtomicInteger

//</doc:import_examples>


class Example_Jobs : Command("utils") {

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
            val users = allUsers.subList(offset.get(), batchSize)

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
        suspend fun sendNewsLetter(task:Task):WorkResult {
            allUsers.forEach { user -> send(NEWS_LETTER_MESSAGE, user) }
            return WorkResult(WorkState.Done)
        }


        // Option 2: Use a function for a job that pages through work
        val offset1 = AtomicInteger(0)
        suspend fun sendNewsLetterWithPaging(task:Task):WorkResult {
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
            // JOB 1: Run to completion
            val job1 = slatekit.jobs.Job(id, listOf(::sendNewsLetter))
            job1.start()

            // JOB 2: Paged Job with event subscriptions
            val job2 = slatekit.jobs.Job(id, listOf(::sendNewsLetterWithPaging))
            job2.subscribe { println("Job ${it.id.name} status changed to : ${it.status()}")}
            job2.subscribe(Status.Complete) { println("Job ${it.id.name} completed")}
            job2.start()

            // JOB 3: Queued + Subscribe to worker status changes
            val queue1 = Queue("sample_queue", Priority.Mid, QueueSourceInMemory.stringQueue(5))
            val job3 = slatekit.jobs.Job(id, listOf(::sendNewsLetterFromQueue), queue1)
            job3.workers.subscribe { it ->  println("Worker ${it.id.name}")}
            job3.workers.subscribe { it ->  println("Worker ${it.id.name} completed")}
            job3.start()

            // JOB 4: Worker implementation with queue
            val queue2 = Queue("sample_queue", Priority.Mid, QueueSourceInMemory.stringQueue(5))
            val job4 = slatekit.jobs.Job(id, listOf(NewsLetterWorker()), queue2)
            job4.start()

            // Delay for 30 seconds
            delay(30000)
        }
        //</doc:examples>
        return Success("")
    }
}