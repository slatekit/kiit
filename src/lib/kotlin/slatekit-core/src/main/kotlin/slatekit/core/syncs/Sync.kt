package slatekit.core.syncs

import slatekit.common.DateTime
import slatekit.common.ext.durationFrom
import slatekit.common.log.LogSupport
import slatekit.common.log.Logger
import slatekit.common.functions.Function
import slatekit.common.functions.FunctionTriggers
import slatekit.common.functions.FunctionInfo
import slatekit.common.functions.FunctionMode
import slatekit.results.Notice
import slatekit.results.Success
import slatekit.results.Try
import slatekit.results.builders.Notices
import slatekit.results.getOrElse
import java.util.concurrent.atomic.AtomicReference


open class Sync(
        override val info: FunctionInfo,
        val settings: SyncSettings,
        val call: SyncCallback = null )
    : Function, FunctionTriggers<SyncResult>, LogSupport {

    override val logger: Logger? = null
    protected var lastSyncTime: DateTime? = null
    protected var lastSyncMode: FunctionMode = FunctionMode.Called
    protected var lastSyncResult = Notices.success(0)
    private var isInProgress = false

    /**
     * Convenience instantiation via just name and desc
     */
    constructor(name: String, desc: String,
                settings: SyncSettings = SyncSettings(true, 60, ""))
            : this(name, desc, settings, null)

    /**
     * Convenience instantiation via just name and desc
     */
    constructor(name: String, desc: String,
                settings: SyncSettings = SyncSettings(true, 60, ""),
                call: SyncCallback = null )
            : this(FunctionInfo(name, desc), settings, call)



    /**
     * Stores the last result
     */
    private val lastResult = AtomicReference<SyncResult>(SyncResult.empty(info))


    /**
     * Stores the last status
     */
    private val lastStatus = AtomicReference<SyncState>(SyncState.empty(info))


    /**
     * Expose the immutable last execution result of this sync
     * @return
     */
    fun lastResult(): SyncResult = lastResult.get()

    /**
     * Expose the last known status of this sync
     * @return
     */
    fun lastStatus(): SyncState = lastStatus.get()


    override fun execute(args: Array<String>, mode: FunctionMode) {
        Try.attempt {
            val canSync = canExecute().success
            val run = canSync || mode == FunctionMode.Forced
            if (run) {
                lastSyncTime = DateTime.now()
                lastSyncMode = mode
                isInProgress = true
                val rawValue = when(call){
                    null -> perform(this::onComplete)
                    else -> call.invoke({ r -> onComplete(r)})
                }
                Success(true, "Forced sync")
            }
        }.onFailure {
            val err = Notices.errored<Int>(it.message ?: "Error executing : ${info.name}")
            this.onComplete(err)
        }
    }


    /**
     * Determines if a sync can be run based on time elapsed and if one is currently running
     * @return
     */
    open fun canExecute(): Notice<Boolean> {
        if (isInProgress) return Notices.errored("Sync in progress")
        if (!settings.enabled) return Notices.errored("Sync is disabled")
        return when (hasTimeElapsed()) {
            true -> Notices.success(true, "Ok to run")
            false -> Notices.errored("Sync interval has not elapsed")
        }
    }


    /**
     * Performs the sync with a supplied callback
     * NOTE: Switch to Co-Routines at some point
     */
    protected open fun perform(onComplete: SyncCompletion) {
    }


    protected open fun onComplete(result: Notice<Int>) {
        val start = lastSyncTime ?: DateTime.now()
        val end = DateTime.now()
        val last = lastResult()
        val curr = SyncResult(last.count + result.getOrElse { 0 }, info, lastSyncMode, result, start, end)

        lastSyncResult = result
        isInProgress = false
        lastSyncMode = FunctionMode.Called

        track(curr)
        handle(curr)
    }


    /**
     * handles the result of the command. this is a workflow hook for derived classes
     * this could be where you add diagnostics
     * @param result
     * @return
     */
    protected open fun handle(result: SyncResult): SyncResult {
        return result
    }


    /**
     * track the result internally, always storing the last result
     * @param result
     * @return
     */
    protected open fun track(result: SyncResult): SyncResult {
        val last = lastStatus()
        val curr = last.update(result)
        lastStatus.set(curr)
        lastResult.set(result)
        return result
    }


    protected open fun hasTimeElapsed(): Boolean {
        val sync = lastSyncTime
        return when (sync) {
            null -> true
            else -> diff(sync) > settings.reloadInSeconds
        }
    }


    private fun diff(lastSync: DateTime): Long {
        val diff = lastSync.durationFrom(DateTime.now()).seconds
        return diff
    }
}