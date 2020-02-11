package slatekit.jobs

import kotlinx.coroutines.runBlocking
import java.util.*
import slatekit.common.Identity
import slatekit.common.Status
import slatekit.common.log.Logger
import slatekit.jobs.support.Command
import slatekit.jobs.support.Coordinator

/**
 * Represents all operations to control / manage a job.
 *
 * 1. starting a job
 * 2. stopping a job
 * 3. pausing  a job
 * 4. resuming a job
 * 5. processing 1 item of the job
 *
 * NOTES:
 * 1. These operations are sent as @see[slatekit.jobs.Action]s to the coordinator
 * 2. The coordinator handles concurrent operations using Kotlin Channels
 * 3. These operations are then dispatched to the individual workers in this job
 *
 */
interface Managed {

    val logger: Logger?

    val coordinator:Coordinator

    /**
     * Run the job by starting it first and then managing it by listening for requests
     */
    suspend fun run()

    /**
     * Requests starting of the job
     */
    suspend fun start() = request(Action.Start)

    /**
     * Requests stopping of the job
     */
    suspend fun stop() = request(Action.Stop)

    /**
     * Requests pausing of the job
     */
    suspend fun pause() = request(Action.Pause)

    /**
     * Requests resuming of the job
     */
    suspend fun resume() = request(Action.Resume)

    /**
     * Requests processing of the job
     */
    suspend fun process() = request(Action.Process)

    /**
     * Requests an action on the entire job
     */
    suspend fun request(action: Action) {
        val (id, uuid) = nextIds()
        val cmd = Command.JobCommand(id, uuid.toString(), action)
        request(cmd)
    }

    /**
     * Requests an action on a specific worker
     */
    suspend fun request(action: Action, workerId: Identity, desc: String?) {
        val (id, uuid) = nextIds()
        val cmd = Command.WorkerCommand(id, uuid.toString(), action, workerId, 30, desc)
        request(cmd)
    }

    /**
     * logs/handle error state/condition
     */
    suspend fun error(currentStatus: Status, message: String)

    /**
     * Gets the next id for uniquely representing requests
     */
    fun nextIds(): Pair<Long, UUID>


    fun record(name:String, info:List<Pair<String, String>>)

    /**
     * Requests this job to perform the supplied command
     * Coordinator handles requests via kotlin channels
     */
    suspend fun request(command: Command) {
        record("Request", command.structured())
        coordinator.send(command)
    }

    /**
     * Listens to and handles 1 single request
     */
    suspend fun respond() {
        // Coordinator takes 1 request off the channel
        val request = coordinator.poll()
        request?.let {
            runBlocking {
                manage(request, false)
            }
        }
    }

    /**
     * Listens to incoming requests ( name of worker )
     */
    suspend fun manage() {
        coordinator.consume { request ->
            manage(request, false)
        }
    }

    suspend fun manage(command: Command, launch: Boolean = true)
}
