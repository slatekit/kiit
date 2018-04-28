/**
<slate_header>
author: Kishore Reddy
url: https://github.com/kishorereddy/scala-slate
copyright: 2015 Kishore Reddy
license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
desc: a scala micro-framework
usage: Please refer to license on github for more info.
</slate_header>
 */


package slatekit.examples

//<doc:import_required>
import slatekit.common.DateTime
import slatekit.core.workers.Manager
import slatekit.core.workers.Worker
//</doc:import_required>

//<doc:import_examples>
import slatekit.common.Result
import slatekit.common.queues.QueueSourceDefault
import slatekit.common.results.ResultFuncs.ok
import slatekit.common.results.ResultFuncs.success
import slatekit.core.cmds.Cmd
import slatekit.core.workers.WorkerSettings
import slatekit.core.workers.WorkerWithQueue

//</doc:import_examples>


class Example_Workers : Cmd("utils") {

    override fun executeInternal(args: Array<String>?): Result<Any> {
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
        val sys = slatekit.core.workers.System()

        // CASE 1: Register a named worker in the default group "default"
        sys.register(Worker<String>("emailer", callback = {
            // NOTE: Simulating work, do not use thread.sleep in a real environment
            Thread.sleep(500)
            println("email worker: " + DateTime.now().toString())
            success("sent registration confirmation to email")
        }))


        // CASE 2: Register named workers in the group "notifications"
        sys.register("notifications", Worker<String>("message_worker", callback = {
            // NOTE: Simulating work, do not use thread.sleep in a real environment
            Thread.sleep(500)
            println("message worker: " + DateTime.now().toString())
            success("sent message to user")
        }))
        sys.register("notifications", Worker<String>("reminder_worker", callback = {
            // NOTE: Simulating work, do not use thread.sleep in a real environment
            Thread.sleep(500)
            println("reminder worker: " + DateTime.now().toString())
            success("sent reminder to user")
        }))


        // CASE 3: Register named worker with manager in new group
        // You can create / derive your own manager to include logic
        // on how to manage the group. The default simply iterates
        // through the workers and if they are idle simply dispatches
        // to the system to perform work.
        // NOTE: There is always 1 manager per group.
        sys.register("reports",Worker<String>( "Active users", callback = {
            // NOTE: Simulating work, do not use thread.sleep in a real environment
            Thread.sleep(500)
            println("report worker: " + DateTime.now().toString())
            success("generated a report of active users")
        }), manager = Manager("reports", sys))


        // CASE 4: Extend the worker class instead of providing a lambda
        class MyWorker : Worker<String>("custom_1") {
            override fun process(args: Array<Any>?): Result<String> {
                println("custom worker: " + DateTime.now().toString())
                return success("customer worker class")
            }
        }
        sys.register("custom", MyWorker())


        // CASE 5: Setup a worker to use a queue
        // NOTE: You can you the AWS queue for production
        // Queues have the interface QueueSource and there is a
        // sample QueueSourceDefault available for prototyping/unit-tests purposes.
        // This lambda specifies a converter for the message
        val queue = QueueSourceDefault("queue1", { item -> item.toString().toInt() })

        // Add some sample items to the queue
        (1..10).forEachIndexed{ ndx, _ -> queue.send(ndx) }

        // Register the worker as a WorkerWithQueue and supply a lamda to process
        // each item from the queue. NOTE: The converter supplied to the queue
        // will convert the messageBody to the type T supplied.
        sys.register("queued", WorkerWithQueue<Int>("example", "", queue, null, WorkerSettings(batchSize = 2),
            callback = { value ->
            println("queue worker: " + DateTime.now().toString() + " : " + value )
        }))

        // CASE 6: Start the worker system
        // This calls the start() on each group and continuously
        // calls the manage method on the manager of the group.
        sys.run()

        // Just for demo purposes
        Thread.sleep(4000)

        // COMING SOON:
        // 1. Scheduled workers ( at recurrence intervals )
        //</doc:setup>

        //<doc:examples>
        // Each group and work has its own status ( group can be working,
        // but a specific worker can be idle.

        // ===================================================
        // GROUP: Examples of group lookup and properties
        // CASE 1: Get a group by name
        val group1 = sys.get("notifications")
        println( group1?.name )

        // CASE 2: Get the status of the group
        println( sys.get("notifications")?.state() )

        // CASE 3: Get the size of the group
        println( sys.get("notifications")?.size )

        // CASE 4: Get the manager of the group
        println( sys.get("notifications")?.manager )

        // CASE 5: You can pause, resume, stop, start the group
        // NOTE: The status of the group will be changed immediately
        // however, some workers in the group may already be queued
        // up for working, so they will only be effected after their
        // current execution/run is complete
        group1?.pause(30)
        group1?.resume()
        group1?.stop()
        group1?.start()

        // ===================================================
        // WORKER: Examples of worker lookup and properties
        // CASE 1: Get a named worker in a group
        val worker1 = sys.get("notifications")?.get("message_worker")
        println(worker1?.name)

        // CASE 2: Get a named worker by index
        val worker2 = sys.get("notifications")?.get(1)
        println(worker2?.name)

        // CASE 3: Get the run state of the worker
        println( sys.get("notifications")?.get("message_worker")?.state() )

        // CASE 4: Get the status of a worker which provides more info
        val status = sys.get("notifications")?.get("message_worker")?.status()
        println( status?.name       )
        println( status?.errorCount )
        println( status?.lastResult )
        println( status?.lastRunTime)
        println( status?.runCount   )
        println( status?.status     )

        // CASE 5: You can pause, resume, stop, start the worker
        // NOTE: The status of the worker will be changed immediately
        // however, if the worker is queued up for working, it will
        // only be effected after their current execution/run is complete
        worker1?.pause(30)
        worker1?.resume()
        worker1?.stop()
        worker1?.start()

        // ===================================================
        // SYSTEM: Examples of system level features

        // CASE 5: You can pause, resume, stop, start the group
        // NOTE: The status of the system will be changed immediately
        // however, some workers in the group may already be queued
        // up for working, so they will only be effected after their
        // current execution/run is complete
        sys.pause()
        sys.resume()
        sys.stop()

        // This completes the system and will stop all further processing.
        sys.done()

        //</doc:examples>
        return Success("")
    }
}

