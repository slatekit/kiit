package slatekit.jobs.workers

/**
 * Used as a return value for a work function/worker.
 * 1. Done  -> Indicates to system that the worker is done
 * 3. Stop  -> Indicates to system that the worker needs to stop
 * 2. More  -> Indicates to system that the worker finished current task/batch and is yielding but requesting more work ( from a queue )
 * 5. Next  -> Indicates to system that the worker finished current task/batch and is yielding but requesting to work on next batch
 * 4. Delay -> Indicates to the system to delay starting/resuming for x seconds
 *
 * NOTES:
 * 1. The More and Next values allow a worker to "yield" for short time to allow other instances to run
 * 2. The Next value requires the worker to manage its own batches / paging of work
 */
sealed class WorkResult(val name: String) {
    object Unknown : WorkResult("Unknown")
    object Done    : WorkResult("Done")
    object More    : WorkResult("More")
    object Fail    : WorkResult("Fail")
    object Stop    : WorkResult("Stop")
    data class Next(val offset: Long, val processed: Long, val reference: String) : WorkResult("next")

    companion object {
        fun next(offset: Long, processed: Long, reference: String): WorkResult {
            return WorkResult.Next(offset, processed, reference)
        }
    }
}
