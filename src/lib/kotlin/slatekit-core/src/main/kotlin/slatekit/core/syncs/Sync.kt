package slatekit.core.syncs

import slatekit.common.DateTime
import slatekit.common.ext.durationFrom
import slatekit.common.log.LogSupport
import slatekit.common.log.Logger
import slatekit.core.common.FunctionInfo
import slatekit.results.Notice
import slatekit.results.Try
import slatekit.results.builders.Notices
import java.util.concurrent.atomic.AtomicReference


open class Sync(val info: FunctionInfo, protected val settings: SyncSettings) : LogSupport {
    override val logger: Logger? = null
    protected var lastSyncTime: DateTime? = null
    protected var lastSyncResult = Notices.success(true)
    private var isInProgress = false


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


    /**
     * Forces another sync ( this allows for on-demand execution )
     */
    fun force() {
        run("forced", true)
    }

    /**
     * Attempts to run a sync factoring in whether or not the
     * time since last sync has elapsed or if a sync is currently in progress
     */
    fun sync() {
        run("sync", false)
    }

    /**
     * Perform a sync from the server.
     */
    fun run(mode: String, force: Boolean) {
        Try.attempt {
            val canSync = canSync().success
            val run = canSync || force
            if (run) {
                lastSyncTime = DateTime.now()
                isInProgress = true
                perform(this::onComplete)
            }
        }.onFailure {
            val err = Notices.errored<Boolean>(it.message ?: "Error executing : ${info.name}")
            this.onComplete(err)
        }
    }


    /**
     * Determines if a sync can be run based on time elapsed and if one is currently running
     * @return
     */
    open fun canSync(): Notice<Boolean> {
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
    protected open fun perform(onComplete: ((Notice<Boolean>) -> Unit)) {}


    private fun onComplete(result: Notice<Boolean>) {
        lastSyncResult = result
        isInProgress = false
        val start = lastSyncTime ?: DateTime.now()
        val end = DateTime.now()
        val duration = end.durationFrom(start).seconds
        val result = SyncResult(info, result, start, end, duration)
        track(result)
        handle(result)
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
    private fun track(result: SyncResult): SyncResult {
        val last = lastStatus()
        val curr = last.update(result)
        lastStatus.set(curr)
        return result
    }


    private fun hasTimeElapsed(): Boolean {
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