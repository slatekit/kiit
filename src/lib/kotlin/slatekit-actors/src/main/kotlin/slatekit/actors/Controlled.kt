package slatekit.actors

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.util.concurrent.atomic.AtomicReference

/**
 *
 */
abstract class Controlled<T>(override val ctx: Context, val channel: Channel<Message<T>>) : Actor<T>, Controls {

    protected val _status = AtomicReference<Status>(Status.InActive)


    /**
     * Id of the actor e.g. {AREA}.{NAME}.{ENV}.{INSTANCE}
     * e.g. "signup.emails.dev.abc123"
     */
    override val id: String
        get() {
            return ctx.id
        }


    /**
     * Get running status of this Actor
     */
    fun status(): Status = _status.get()


    /**
     * Sends a
     * @param msg  : Full message
     */
    suspend fun send(req: Request<T>) {
        channel.send(req)
    }


    /**
     * Sends a message
     * @param msg  : Full message
     */
    override suspend fun send(msg: Content<T>) {
        channel.send(msg)
    }


    /**
     * Sends a message
     * @param msg  : Full message
     */
    suspend fun send(ctl: Control<T>) {
        channel.send(ctl)
    }


    /**
     * Sends a control message to start, pause, resume, stop processing
     */
    override suspend fun control(action: Action, msg: String?, target: String) : Feedback {
        send(Control<T>(action, msg, target = target))
        return Feedback(true, "")
    }


    override suspend fun work(): Job {
        return ctx.scope.launch {
            for (msg in channel) {
                track(Puller.WORK, msg)
                work(msg)
                yield()
            }
        }
    }


    open suspend fun work(item: Message<T>) {
        when (item) {
            is Control -> {
                control(item)
            }
            is Request -> {
                begin(); request(item)
            }
            is Content -> {
                begin(); work(Action.Process, item.target, item.data)
            }
        }
    }


    protected open suspend fun control(msg: Control<T>) {
        val action = msg.action
        val oldStatus = _status.get()
        val newStatus = action.toStatus(oldStatus)
        if(newStatus == Status.Stopped) {
            println("STOPPING:")
        }
        when (action) {
            is Action.Delay -> move(newStatus)
            is Action.Start -> move(newStatus)
            is Action.Pause -> move(newStatus)
            is Action.Resume -> move(newStatus)
            is Action.Stop -> move(newStatus)
            is Action.Kill -> move(newStatus)
            else -> {

            }
        }
        changed(msg, oldStatus, newStatus)
    }


    protected open suspend fun request(req: Request<T>) {
    }


    protected abstract suspend fun work(action: Action, target: String, item: T)


    protected open suspend fun changed(msg: Control<T>, oldStatus: Status, newStatus: Status) {
    }


    protected open suspend fun track(source: String, data: Message<T>) {
    }


    protected fun move(newStatus: Status) {
        _status.set(newStatus)
    }


    private fun begin() {
        val current = status()
        if (current == Status.Started) {
            move(Status.Running)
        }
    }
}
