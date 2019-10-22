package slatekit.jobs.support

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.yield
import slatekit.common.Status
import slatekit.common.log.Logger
import slatekit.jobs.*
import slatekit.results.Failure
import slatekit.results.Success
import slatekit.results.builders.Tries


/**
 * Coordinates the work loop
 */
interface Coordinator {
    val logger:Logger
    val ids: JobId


    /**
     * Sends a command to manage the job/worker
     */
    suspend fun request(cmd: Command)


    /**
     * Attempts to respond/handle to a command for a job/worker
     */
    suspend fun respondOne(): Command?


    /**
     * Attempts to respond/handle to a command for a job/worker
     */
    suspend fun respond(operation:suspend (Command) -> Unit )
}


class ChannelCoordinator(override val logger: Logger, override val ids: JobId, val channel: Channel<Command>) : Coordinator {

    override suspend fun request(request: Command){
        channel.send(request)
    }


    override suspend fun respondOne(): Command? {
        return channel.receive()
    }


    override suspend fun respond(operation:suspend (Command) -> Unit ) {
        for(request in channel){
            operation(request)
            yield()
        }
    }
}