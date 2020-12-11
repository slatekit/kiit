package slatekit.samples.job

import kotlinx.coroutines.runBlocking
import slatekit.common.Identity
import slatekit.common.Status
import slatekit.jobs.Task
import slatekit.jobs.workers.WorkResult
import slatekit.jobs.workers.Worker
import java.util.concurrent.atomic.AtomicInteger

// Sample User model
data class User(val id:Int, val email:String)

val NEWS_LETTER_MESSAGE = "New version coming out soon!"


// Sample list of 20 users
val allUsers = (1..20).map { User(it, "user$it@company1.com")}



/**
 * =================================================================================================
 * OPTION 1: One Time Job:
 * Use a function for a job that runs to completion: return WorkState.Done
 * =================================================================================================
 */
suspend fun sendNewsLetter(task: Task): WorkResult {
    allUsers.forEach { user -> JobUtils.send(task.job, NEWS_LETTER_MESSAGE, user) }
    return WorkResult.Done
}



/**
 * =================================================================================================
 * OPTION 2: Paged Job
 * Use a function for a job that pages through work : WorkState.Next ( see JobUtils )
 * =================================================================================================
 */
val offset1 = AtomicInteger(0)
suspend fun sendNewsLetterWithPaging(task: Task): WorkResult {

    // Print some info
    println("\nProcessing : ====================================")
    println("offset: " + offset1.get())

    // WorkResult.next(offset.get() + batchSize.toLong(), users.size.toLong(), "users")
    return JobUtils.sendNewsLetterBatch(task.job, offset1, 4)
}



/**
 * =================================================================================================
 * OPTION 3: Queued Job
 * Use a function for a job that processes a task from a queue: Process and return WorkState.More
 * =================================================================================================
 */
suspend fun sendNewsLetterFromQueue(task: Task): WorkResult {
    val userId = task.data.toInt()
    val user = allUsers.first { it.id == userId }
    JobUtils.send(task.job, NEWS_LETTER_MESSAGE, user)

    // Print some info
    JobUtils.showInfo(task)

    // Acknowledge the task or abandon task.fail()
    task.done()

    // Indicate that this can now handle more
    return WorkResult.More
}



/**
 * =================================================================================================
 * OPTION 4: Worker
 * Extend from a worker to have more control over the life-cycle
 * =================================================================================================
 */
class NewsLetterWorker(id:Identity) : Worker<String>(id) {
    private val offset = AtomicInteger(0)

    // Initialization hook ( for setup / logs / alerts )
    override suspend fun started() {
        notify("initializing", listOf(("id" to this.id.name)))
    }

    // Implement your work here.
    // NOTE: If you are not using a queue, this task will be empty e.g. Task.empty
    override suspend fun work(task: Task): WorkResult {
        // Print some info
        JobUtils.showInfo(task)

        return JobUtils.sendNewsLetterBatch(task.job, offset, 4)
    }

    // Transition hook for when the status is changed ( e.g. from Status.Running -> Status.Paused )
    override suspend fun move(state: Status, note:String?) {
        _status.set(Pair(state, state.name))
        notify("move", listOf("status" to state.name))
    }

    // Completion hook ( for logic / logs / alerts )
    override suspend fun completed() {
        notify("done", listOf(("id" to this.id.name)))
    }

    // Failure hook ( for logic / logs / alerts )
    override suspend fun failed(err:Throwable?) {
        notify("failure", listOf(("id" to this.id.name), ("err" to (err?.message ?: ""))))
    }

    // Initialization hook ( for setup / logs / alerts )
    override suspend fun notify(desc: String?, extra: List<Pair<String, String>>?) {
        val detail = extra?.joinToString(",") { it.first + "=" + it.second }
        // Simulate notification to email/alerts/etc
        println(desc + detail)
    }
}


/**
 * Utilities for this Job
 */
object JobUtils {

    fun showInfo(task: Task) {

        println("\nProcessing : ====================================")
        println("job.id  : " + task.id)    // abc123
        println("job.from: " + task.from)  // queue://notification
        println("job.job : " + task.job)   // job1
        println("job.name: " + task.name)  // users.sendNewsletter
        println("job.data: " + task.data)  // { ... } json payload
        println("job.xid : " + task.xid)   // 12345   correlation id
        println("\n")

    }

    // Sends the message ( newsletter ) to the user
    suspend fun send(sender:String, msg:String, user: User) {
        // Simulate sending message to user
        runBlocking { println("Source: ${sender}: Sent $msg to ${user.email}") }
    }


    // NOTE: This is a helper method used for the real example(s) below
    suspend fun sendNewsLetterBatch(sender:String, offset: AtomicInteger, batchSize:Int): WorkResult {
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
}





