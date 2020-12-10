package slatekit.core.slatekit.core.actors

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.yield
import java.util.concurrent.atomic.AtomicReference

abstract class Actor<T>(private val channel: Channel<Message<T>>) : Ops {

    private val _status = AtomicReference<Status>(Status.InActive)


    fun status(): Status = _status.get()


    suspend fun send(item:T) {
        send(Message.Perform<T>(item))
    }


    override suspend fun send(action: Action, msg:String?) {
        send(Message.Control<T>(action, msg))
    }


    suspend fun send(msg: Message<T>) {
        channel.send(msg)
    }


    suspend fun work() {
        for (msg in channel) {
            track(Control.WORK, msg)
            work(msg)
            yield()
        }
    }


    suspend fun work(msg: Message<T>) {
        when (msg) {
            is Message.Control -> control(msg)
            is Message.Perform -> {
                val status = _status.get()
                if (status == Status.Running) {
                    work(msg.data)
                }
            }
        }
    }


    abstract suspend fun work(data: T)


    open suspend fun track(source: String, item: Message<T>) {
    }


    open suspend fun changed(oldStatus: Status, newStatus: Status) {

    }


    private suspend fun control(msg: Message.Control<T>) {
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
        changed(oldStatus, newStatus)
        if (this is Cycle) {
            cycle(msg, oldStatus, newStatus, this as Cycle)
        }
    }


    private suspend fun cycle(msg: Message.Control<T>, oldStatus: Status, newStatus: Status, cycle: Cycle) {
        when (newStatus) {
            is Status.InActive -> {
            }
            is Status.Started -> cycle.started()
            is Status.Paused -> cycle.paused(msg.msg)
            is Status.Stopped -> cycle.stopped(msg.msg)
            is Status.Completed -> cycle.completed(msg.msg)
            is Status.Failed -> cycle.failed(msg.msg)
            is Status.Killed -> cycle.killed(msg.msg)
            is Status.Running -> {
                if (oldStatus == Status.Stopped) {
                    cycle.resumed(msg.msg)
                }
            }
        }
    }
}
