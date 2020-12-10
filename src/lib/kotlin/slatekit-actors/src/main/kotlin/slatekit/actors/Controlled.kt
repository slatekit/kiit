package slatekit.actors

import kotlinx.coroutines.channels.Channel
import java.util.concurrent.atomic.AtomicReference

abstract class Controlled<T>(context: Context, channel: Channel<Message<T>>) : Base<T>(context, channel), Controls {

    protected val _status = AtomicReference<Status>(Status.InActive)


    fun status(): Status = _status.get()


    /**
     * Sends a control message to start, pause, resume, stop processing
     */
    override suspend fun control(action: Action, msg:String?, target:String) {
        channel.send(Control<T>(action, msg, target = target))
    }


    override suspend fun work(item: Message<T>) {
        when (item) {
            is Control -> control(item)
            is Request -> request(item)
            is Content -> {
                val status = _status.get()
                if (status == Status.Running) {
                    handle(item)
                }
            }
        }
    }


    protected open suspend fun changed(msg:Control<T>, oldStatus: Status, newStatus: Status) {}


    protected abstract suspend fun request(req:Request<T>)


    protected abstract suspend fun handle(item:Content<T>)


    protected abstract suspend fun handle(action: Action, target: String, item: T)


    protected open suspend fun control(msg: Control<T>) {
        val action = msg.action
        val oldStatus = _status.get()
        val newStatus = action.toStatus(oldStatus)
        when (action) {
            is Action.Delay -> _status.set(newStatus)
            is Action.Start -> _status.set(newStatus)
            is Action.Pause -> _status.set(newStatus)
            is Action.Resume -> _status.set(newStatus)
            is Action.Stop -> _status.set(newStatus)
            is Action.Kill -> _status.set(newStatus)
            is Action.Check -> {
            }
        }
        changed(msg, oldStatus, newStatus)
    }
}
