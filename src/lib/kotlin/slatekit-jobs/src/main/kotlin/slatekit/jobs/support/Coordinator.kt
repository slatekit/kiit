package slatekit.jobs.support

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.yield
import slatekit.common.ids.Paired
import slatekit.common.log.Logger
import slatekit.jobs.*

/**
 * Coordinates the work loop.
 * Coordination of work is controlled by sending Commands ( of @see[slatekit.jobs.support.Command.JobCommand]
 * or @see[slatekit.jobs.support.Command.WorkerCommand] to an in-memory Queue ).
 */
interface Coordinator {
    val logger: Logger
    val ids: Paired

    /**
     * Sends a command to manage the job/worker
     */
    suspend fun send(cmd: Command)

    /**
     * Attempts to respond/handle to a command for a job/worker
     */
    suspend fun poll(): Command?

    /**
     * Attempts to respond/handle to a command for a job/worker
     */
    suspend fun consume(operation: suspend (Command) -> Unit)
}


class ChannelCoordinator(override val logger: Logger, override val ids: Paired, val channel: Channel<Command>) : Coordinator {

    override suspend fun send(request: Command) {
        channel.send(request)
    }

    override suspend fun poll(): Command? {
        return channel.poll()
    }

    override suspend fun consume(operation: suspend (Command) -> Unit) {
        for (cmd in channel) {
            operation(cmd)
            yield()
        }
    }
}
