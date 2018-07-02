package slatekit.core.workers

import slatekit.common.status.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.TimeUnit



open class System(service:ExecutorService? = null, val settings:SystemSettings = SystemSettings())
    : Runnable
{
    private val _runState = AtomicReference<RunState>(RunStateNotStarted)
    private var _thread:Thread? = null

    /**
     * You can extend the work system and
     */
    open val svc = service ?: Executors.newFixedThreadPool(3)


    /**
     * organizes all the workers into groups
     */
    private val groups = mutableMapOf<String, Group>()


    /**
     *
     */
    val defaultGroup = "default"


    /**
     * register a worker into the default group
     */
    fun <T> register(worker:Worker<T>, manager:Manager? = null):Unit {
        register(defaultGroup, worker, manager)
    }


    /**
     * register a worker into the default group
     */
    fun <T> register(groupName:String, worker:Worker<T>, manager:Manager? = null):Unit {
        val group = getOrCreate(groupName, manager)
        group.add(worker.name, worker)
    }


    /**
     * Gets the group with the supplied name
     */
    fun get(group:String):Group? = groups[group]


    /**
     * Gets the worker in the group supplied
     */
    fun get(group:String, worker:String):Worker<*>? = get(group)?.get(worker)


    /**
     * runs all the workers in the groups within the work-flow of
     * init, exec, end
     */
    override fun run():Unit {

        // Initialize
        moveToState(RunStateInitializing)
        init()

        // Work
        moveToState(RunStateBusy)
        var state = _runState.get()
        while(state != RunStateComplete) {

            // Prevent paused/stopped status from executing logic
            if(state == RunStateBusy) {
                exec()
            }

            // Enable pause ?
            if(settings.pauseBetweenCycles) {
                Thread.sleep(settings.pauseTimeInSeconds * 1000L)
            }
            state = _runState.get()
        }

        // Ending/Complete
        moveToState(RunStateComplete)
        end()
    }


    /**
     * initialize the system.
     * NOTE: This is open to allow derived classes to self register
     * all workers and groups and have them ready to be run later
     */
    open fun init():Unit {
        println("initializing")
        if(settings.enableAutoStart) {
            groups.forEach{ _, group ->
                group.start()
            }
        }
    }


    /**
     * performs the core logic of executing all the workers in all the groups.
     * NOTE: This is open to allow derived classes more fine grained
     * control and to handle custom execution of all the groups/workers
     */
    open fun exec():Unit {
        TODO.IMPROVE("workers", "Move manager.manage to thread pool as well")
        groups.forEach { _, group ->
            group.manager.manage()
        }
    }


    /**
     * stops the system.
     * NOTE: This is open to allow derived classes to handle
     * any shutdown / end steps
     */
    open fun end():Unit {
        println("ending")
        groups.forEach{ _, group ->
            group.stop()
        }
    }


    fun start():Unit {
        _thread = Thread(this)
        _thread?.isDaemon = true
        _thread?.start()
    }


    /**
     * pauses the system
     */
    fun pause() = {
        moveToState(RunStatePaused)
    }


    /**
     * resumes the system
     */
    fun resume() = {
        moveToState(RunStateBusy)
    }


    /**
     * stops the system
     */
    fun stop() = {
        moveToState(RunStateStopped)
    }


    /**
     * pauses the system
     */
    fun done() {
        moveToState(RunStateComplete)

        // Graceful shutdown
        svc.shutdown()
        try {
            if (!svc.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                svc.shutdownNow()
            }
        } catch (e: InterruptedException) {
            svc.shutdownNow()
        }
    }


    /**
     * starts the group
     */
    fun start(group:String) = get(group)?.start()


    /**
     * pauses the group
     */
    fun pause(group:String) = get(group)?.pause()


    /**
     * resumes the group
     */
    fun resume(group:String) =  get(group)?.resume()


    /**
     * stops the group
     */
    fun stop(group:String) =  get(group)?.stop()


    /**
     * starts the worker in the group supplied
     */
    fun start(group:String, worker:String) = get(group)?.get(worker)?.start()


    /**
     * pauses the worker in the group supplied
     */
    fun pause(group:String, worker:String) = get(group)?.get(worker)?.pause()


    /**
     * resumes the worker in the group supplied
     */
    fun resume(group:String, worker:String) = get(group)?.get(worker)?.resume()


    /**
     * stops the worker in the group supplied
     */
    fun stop(group:String, worker:String) = get(group)?.get(worker)?.stop()


    /**
     * Sends the worker to work by using the ThreadPool
     */
    fun sendToWork(worker:Worker<*>) {
        svc.execute(worker)
    }


    /**
     * moves the current state to the name supplied and performs a status update
     *
     * @param state
     * @return
     */
    fun moveToState(state: RunState): RunState {
        _runState.set(state)
        return state
    }


    private fun getOrCreate(name:String, manager:Manager? = null):Group {
        return if(groups.containsKey(name)) {
            groups[name]!!
        }
        else {
            val group = Group(name, this, manager)
            groups[name] = group
            group
        }
    }
}
