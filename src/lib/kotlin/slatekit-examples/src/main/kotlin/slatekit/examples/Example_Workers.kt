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
import slatekit.common.*
import slatekit.common.log.LogsDefault
import slatekit.common.metrics.MetricsLite
//</doc:import_required>

//<doc:import_examples>
import slatekit.common.queues.QueueSourceInMemory
import slatekit.common.queues.QueueValueConverter
import slatekit.core.cmds.Command
import slatekit.common.CommonContext
import slatekit.core.cmds.CommandRequest
import slatekit.results.Try
import slatekit.results.Success

//</doc:import_examples>


class Example_Workers : Command("utils") {

    override fun execute(request: CommandRequest): Try<Any> {
        TODO.IMPLEMENT("jobs", "Example")
        //<doc:setup>
        // The background workers system is designed with a few basic
        // principles and concepts:
        //
        // TERMINOLOGY:
        // 1. Worker  : Actual background work that handles some work
        // 2. Group   : Named collection of workers for organization
        // 3. Manager : Manages a group and ensures each idle worker perform work
        // 4. System  : Top level system that runs workers in a java executor service
        // 5. Queued  : Interface for handling work from a queue
//        val sys = slatekit.workers.System(
//                CommonContext.sample("test", "", "", ""),
//                listOf(),
//                metrics = MetricsLite.build())
//
//        // CASE 1: Register a named worker in the default group "default"
//        sys.register(Worker<String>(
//            "emailer", group = "notifications", version = "1.0",
//            desc = "Sends out email notifications",
//            work = {
//            // NOTE: Simulating work, do not use thread.sleep in a real environment
//            Thread.sleep(500)
//            println("email worker: " + DateTime.now().toString())
//            Success("sent registration confirmation to email")
//        }, logs = LogsDefault))
//
//
//        // CASE 2: Register named workers in the group "notifications"
//        sys.register(Worker<String>(
//            "message_worker", group = "notifications" , version = "1.0",
//            desc = "Sends out push notifications for message feeds",
//            work = {
//            // NOTE: Simulating work, do not use thread.sleep in a real environment
//                Thread.sleep(500)
//                println("message worker: " + DateTime.now().toString())
//                Success("sent message to user")
//            }, logs = LogsDefault
//        ))
//        sys.register(Worker<String>(
//            "reminder_worker", group = "notifications" , version = "1.0",
//            desc = "Sends out push notifications of reminders",
//            work = {
//                // NOTE: Simulating work, do not use thread.sleep in a real environment
//                Thread.sleep(500)
//                println("reminder worker: " + DateTime.now().toString())
//                Success("sent reminder to user")
//            }, logs = LogsDefault
//        ))
//
//
//        // CASE 3: Register named worker with manager in new group
//        // You can create / derive your own manager to include logic
//        // on how to manage the group. The default simply iterates
//        // through the workers and if they are idle simply dispatches
//        // to the system to perform work.
//        // NOTE: There is always 1 manager per group.
//        sys.register(Worker<String>(
//            "Active users", group = "reports", version = "1.0",
//            desc = "Generates a report that determines active users",
//            work = {
//                // NOTE: Simulating work, do not use thread.sleep in a real environment
//                Thread.sleep(500)
//                println("report worker: " + DateTime.now().toString())
//                Success("generated a report of active users")
//            }, logs = LogsDefault
//        ))
//
//
//        // CASE 4: Extend the worker class instead of providing a lambda
//        class MyWorker : Worker<String>(
//            name ="custom_1", group = "reports", version = "1.0",
//            desc = "Generates a report that determines active users", logs = LogsDefault) {
//
//            override fun perform(job: Job): Try<String> {
//                println("custom worker: " + DateTime.now().toString())
//                return Success("customer worker class")
//            }
//        }
//        sys.register(MyWorker())
//
//
//        // CASE 5: Setup a worker to use a queue
//        // NOTE: You can you the AWS queue for production
//        // Queues have the interface QueueSource and there is a
//        // sample QueueSourceDefault available for prototyping/unit-tests purposes.
//        // This lambda specifies a converter for the message
//        val converter = object : QueueValueConverter<Int> {
//            override fun convertToString(item:Int?):String? = item?.toString()
//            override fun convertFromString(content:String?):Int? = content?.toInt()
//        }
//        val queue = QueueSourceInMemory<Int>("queue1", converter)
//
//        // Add some sample items to the queue
//        (1..10).forEachIndexed{ ndx, _ -> queue.send(ndx) }
//
//        // Register the worker as a WorkerWithQueue and supply a lamda to process
//        // each item from the queue. NOTE: The converter supplied to the queue
//        // will convert the messageBody to the type T supplied.
//        sys.register( Worker<Int>(
//            "queued", "", "", version = "1.0",
//            settings = WorkerSettings(batchSize = 2),
//            work = { value ->
//                println("queue worker: " + DateTime.now().toString() + " : " + value )
//                Success(1)
//            }, logs = LogsDefault
//        ))
//
//        // CASE 6: Start the worker system
//        // This calls the start() on each group and continuously
//        // calls the manage method on the manager of the group.
//        sys.run()
//
//        // Just for demo purposes
//        Thread.sleep(4000)
//
//        // COMING SOON:
//        // 1. Scheduled workers ( at recurrence intervals )
//        //</doc:setup>
//
//        //<doc:examples>
//        // Each group and work has its own status ( group can be working,
//        // but a specific worker can be idle.
//
//        // ===================================================
//        // GROUP: Examples of group lookup and properties
//        // CASE 1: Get a group by name
//        val group1 = sys.get("message_worker")
//        println( group1?.about )
//
//        // CASE 2: Get the status of the worker
//        println( sys.get("message_worker")?.status() )
//
//        // CASE 3: Get the stats of the worker
//        println( sys.get("message_worker")?.status() )
//
//        // CASE 5: You can pause, resume, stop, start the group
//        // NOTE: The status of the group will be changed immediately
//        // however, some workers in the group may already be queued
//        // up for working, so they will only be effected after their
//        // current execution/run is complete
//        group1?.pause()
//        group1?.resume()
//        group1?.stop()
//        group1?.start()
//
//        // ===================================================
//        // WORKER: Examples of worker lookup and properties
//        // CASE 1: Get a named worker in a group
//        val worker1 = sys.get("message_worker")
//        println(worker1?.about?.name)
//
//        // CASE 2: Get the run state of the worker
//        println( sys.get("message_worker")?.status() )
//
//        // CASE 3: Get the status of a worker which provides more info
//        val status = sys.get("message_worker")?.stats()
//        println( status?.name             )
//        println( status?.lastRunTime      )
//        println( status?.lastResult       )
//        println( status?.lastRequest      )
//        println( status?.lastSuccess      )
//        println( status?.lastFiltered     )
//        println( status?.lastErrored      )
//        println( status?.totals           )
//
//        // CASE 5: You can pause, resume, stop, start the worker
//        // NOTE: The status of the worker will be changed immediately
//        // however, if the worker is queued up for working, it will
//        // only be effected after their current execution/run is complete
//        worker1?.pause()
//        worker1?.resume()
//        worker1?.stop()
//        worker1?.start()
//
//        // ===================================================
//        // SYSTEM: Examples of system level features
//
//        // CASE 5: You can pause, resume, stop, start the group
//        // NOTE: The status of the system will be changed immediately
//        // however, some workers in the group may already be queued
//        // up for working, so they will only be effected after their
//        // current execution/run is complete
//        sys.pause()
//        sys.resume()
//        sys.stop()
//
//        // This completes the system and will stop all further processing.
//        sys.done()

        //</doc:examples>
        return Success("")
    }
}

