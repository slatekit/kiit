package slatekit.core.workers

import slatekit.common.DateTime
import slatekit.common.ListMap
import slatekit.common.status.*
import java.util.concurrent.atomic.AtomicReference

/**
 * A group is a simple collection of workers.
 * This allows for organization and management of workers
 * at a group level.
 */
open class Group(val name:String, val sys: System, manager:Manager? = null) : RunStatusSupport {

    protected val _runState = AtomicReference<RunState>(RunStateNotStarted)
    protected val _runStatus = AtomicReference<RunStatus>(RunStatus())


    /**
     * Manages the group.
     */
    val manager = manager ?: Manager(name, sys)


    /**
     * stores workers in this group
     */
    private var _workers = ListMap<String, Worker<*>>()


    /**
     * adds a worker to the group
     */
    fun add(name: String, worker: Worker<*>): Unit {
        _workers = _workers.add(name, worker)
    }


    /**
     * determines whether or not this group contains the worker with the name.
     */
    fun contains(name: String): Boolean = _workers.contains(name)


    /**
     * removes the worker by its name
     */
    fun remove(name: String): Unit {
        _workers = _workers.remove(name)
    }


    /**
     * gets the number of workers
     */
    val size: Int get() = _workers.size


    /**
     * gets a worker by its index position
     */
    operator fun get(ndx: Int): Worker<*>? = _workers.getAt(ndx)


    /**
     * Gets a worker by its name
     */
    operator fun get(key: String): Worker<*>? = _workers.get(key)


    val all: List<Worker<*>> get() = _workers.all()


    /**
     * gets the current state of execution
     *
     * @return
     */
    override fun state(): RunState = _runState.get()


    /**
     * gets the current status of the application
     *
     * @return
     */
    override fun status(): RunStatus = _runStatus.get()


    /**
     * starts this group of workers and moves the group state to working
     */
    override fun start(): RunStatus {
        all.forEach { it.start() }
        return moveToState(RunStateBusy)
    }


    /**
     * pauses this group of workers and moves the group state to paused
     */
    override fun pause(seconds:Int): RunStatus {
        all.forEach { it.pause() }
        return moveToState(RunStatePaused)
    }


    /**
     * resumes this group of workers and moves the group state to working
     */
    override fun resume(): RunStatus {
        all.forEach { it.resume() }
        return moveToState(RunStateIdle)
    }


    /**
     * stops this group of workers and moves the group state to stopped
     */
    override fun stop():RunStatus {
        all.forEach{ it.stop() }
        return moveToState(RunStateStopped)
    }


    /**
     * moves the current state to the name supplied and performs a status update
     *
     * @param state
     * @return
     */
    override fun moveToState(state: RunState): RunStatus {
        val last = _runStatus.get()
        _runState.set(state)
        _runStatus.set(RunStatus(name, DateTime.now(), state.mode, last.runCount + 1, last.runCount, ""))
        return _runStatus.get()
    }


    fun forEach(callback:(Worker<*>) -> Unit) = all.forEach{ callback(it) }
}