package slatekit.workers

import slatekit.common.log.LogsDefault
import slatekit.common.metrics.MetricsLite
import slatekit.common.queues.QueueSourceInMemory
import slatekit.results.Success


fun main(args:Array<String>){

    // Define all the queues
    val queues = Queues(
            listOf(
                    Queue("registration" , Priority.High  , QueueSourceInMemory.stringQueue()),
                    Queue("messaging"    , Priority.Medium, QueueSourceInMemory.stringQueue()),
                    Queue("updates"      , Priority.Medium, QueueSourceInMemory.stringQueue())
            )
    )

    // Define workers in different ways

    // Case 1: Worker to handle any queue ( without specifying the queue names )
    val worker1 = Worker<String>("worker_1", "general")

    // Case 2: Worker to handle a named queue
    val worker2 = Worker<String>("worker_2", "registration", listOf("registration"))

    // Case 3: Worker to handle multiple named queues
    val worker3 = Worker<String>("worker_3", "messaging", listOf("updates", "emails"))

    // Case 4: Worker with full settings
    val worker4 = Worker<String>(
            name = "worker_3",
            group = "group1",
            desc = "handle all messaging / notifications",
            version = "1.0",
            queues = listOf("updates", "emails"),
            work = { job:Job -> Success("Processed job : ${job.id}") },
            settings = WorkerSettings(),
            logs = LogsDefault,
            metrics = MetricsLite()
    )
}