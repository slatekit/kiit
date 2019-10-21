package slatekit.jobs

import slatekit.common.Status
import slatekit.common.Identity
import slatekit.jobs.support.Command
import java.util.*


/**
 * Represents all operations to control / manage a job
 */
interface Management {

    /**
     * Run the job by starting it first and then managing it by listening for requests
     */
    suspend fun run()

    /**
     * Requests starting of the job
     */
    suspend fun start() = request(JobAction.Start)

    /**
     * Requests stopping of the job
     */
    suspend fun stop() = request(JobAction.Stop)

    /**
     * Requests pausing of the job
     */
    suspend fun pause() = request(JobAction.Pause)

    /**
     * Requests resuming of the job
     */
    suspend fun resume() = request(JobAction.Resume)

    /**
     * Requests processing of the job
     */
    suspend fun process() = request(JobAction.Process)

    /**
     * Requests an action on the entire job
     */
    suspend fun request(action: JobAction) {
        val (id, uuid) = nextIds()
        val req = Command.JobCommand(id, uuid.toString(), action)
        request(req)
    }

    /**
     * Requests an action on a specific worker
     */
    suspend fun request(action: JobAction, workerId: Identity, desc:String?) {
        val (id, uuid) = nextIds()
        val req = Command.WorkerCommand(id, uuid.toString(), action, workerId, 30, desc)
        request(req)
    }

    /**
     * Requests an action to manage a job/worker
     */
    suspend fun request(command: Command)


    /**
     * Listens to and handles 1 request
     */
    suspend fun respond()

    /**
     * Listens to and handles all incoming requests
     */
    suspend fun manage()

    /**
     * logs/handle error state/condition
     */
    suspend fun error(currentStatus:Status, message:String)


    /**
     * Gets the next id for uniquely representing requests
     */
    fun nextIds():Pair<Long, UUID>
}