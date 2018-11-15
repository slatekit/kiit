package slatekit.workers

import slatekit.common.Context
import slatekit.common.info.About
import slatekit.common.metrics.Metrics
import slatekit.workers.core.QueueInfo
import slatekit.workers.core.Stats
import slatekit.workers.status.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.TimeUnit

/**
 * This is the top-level class in the worker system and supports the following use cases:
 * TODO: Document design goal for this class
 * 1. register a new worker into a group
 * 2. get a specific group in the system
 * 3. get a specific group.worker in the system
 * 4. run the life-cycle methods on groups/workers ( e..g init, end )
 * 5. control a specific group  life-cycle methods ( start, stop, pause, resume )
 * 6. control a specific worker life-cycle methods ( start, stop, pause, resume )
 */
open class System(
        val ctx: Context,
        val queueInfos: List<QueueInfo>,
        service: ExecutorService? = null,
        val managerCreator: ((System) -> Manager)? = null,
        val settings: SystemSettings = SystemSettings(),
        val metrics: Metrics
        ) : Runnable {


    private val _runState = AtomicReference<Status>(Status.InActive)
    private var _thread: Thread? = null


    /**
     * Queues built from queue infos with priorities, creating "weighted" queues.
     */
    val queues = Queues(queueInfos)


    /**
     * Log for system level actions :
     * 1. initialization/execution/shutdown
     * 2. transition changes
     * 3. worker management
     */
    val log = ctx.logs.getLogger(this.javaClass.name)


    /**
     * Used as a prefix for all logs here specifically for status changes.
     */
    val logPrefix = "WorkSystem: status: "


    /**
     * Wraps an operation with useful logging indicating starting/completion of action
     */
    val perform = { name: String, action: () -> Unit ->
        log.info("$name starting")
        action()
        log.info("$name complete")
    }

    /**
     * You can extend the work system and
     */
    open val svc = service ?: Executors.newFixedThreadPool(3)


    /**
     * organizes all the workers into groups
     */
    private val workers = mutableMapOf<String, Worker<*>>()

    /**
     * register a worker into the default group
     */
    fun <T> register(worker: Worker<T>) {
        workers[worker.id] = worker
        log.info("registered worker ${worker.id}")
    }

    /**
     * Gets the worker with the supplied name
     */
    fun get(name: String): Worker<*>? = workers[name]

    fun getWorkers(): List<Worker<*>> = workers.values.toList()

    fun getWorkerNames(): List<About> = workers.values.map { it.about }

    fun getWorkerStats(): List<Stats> = workers.values.map { it.stats() }

    /**
     * Start up and run all the background workers
     */
    override fun run() {
        exec()
    }

    /**
     * initialize the system.
     * NOTE: This is open to allow derived classes to self register
     * all workers and groups and have them ready to be run later
     */
    open fun init() {
        perform("$logPrefix initialization") {
            workers.forEach { id, worker ->
                log.info("initializing worker $id")
                worker.init()
            }
        }
    }

    /**
     * performs the core logic of executing all the workers in all the groups.
     * NOTE: This is open to allow derived classes more fine grained
     * control and to handle custom execution of all the groups/workers
     */
    open fun exec() {

        // Initialize
        moveToState(Status.Starting)
        init()

        // Work
        moveToState(Status.Running)

        // Move workers to running state
        perform("$logPrefix starting workers") {
            workers.forEach { id, worker ->
                log.info("moving worker to running $id")
                worker.moveToState(Status.Running)
            }
        }

        // Get the instance of the runner
        val manager = managerCreator?.invoke(this) ?: DefaultManager(this)
        manager.manage(this)

        // Ending/Complete
        moveToState(Status.Complete)
        end()
    }

    /**
     * stops the system.
     * NOTE: This is open to allow derived classes to handle
     * any shutdown / end steps
     */
    open fun end() {
        perform("$logPrefix shutdown") {
            workers.forEach { id, worker ->
                log.info("shutting down worker $id")
                worker.end()
            }
        }
    }

    fun start() {
        _thread = Thread(this)
        _thread?.start()
    }

    /**
     * pauses the system
     */
    fun pause() = {
        moveToState(Status.Paused)
    }

    /**
     * resumes the system
     */
    fun resume() = {
        moveToState(Status.Running)
    }

    /**
     * stops the system
     */
    fun stop() = {
        moveToState(Status.Stopped)
    }

    /**
     * pauses the system
     */
    fun done() {
        moveToState(Status.Complete)

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
     * starts the worker
     */
    fun startWorker(worker: String) = perform("starting worker: $worker") { get(worker)?.start() }

    /**
     * pauses the worker
     */
    fun pauseWorker(worker: String) = perform("pausing worker: $worker") { get(worker)?.pause() }

    /**
     * resumes the worker
     */
    fun resumeWorker(worker: String) = perform("resuming worker: $worker") { get(worker)?.resume() }

    /**
     * stops the worker
     */
    fun stopWorker(worker: String) = perform("stopping worker: $worker") { get(worker)?.stop() }


    fun getState(): Status = _runState.get()

    /**
     * moves the current state to the name supplied and performs a status update
     *
     * @param state
     * @return
     */
    fun moveToState(state: Status): Status {
        _runState.set(state)
        log.info("$logPrefix transitioning to ${state.name}")
        return state
    }
}
