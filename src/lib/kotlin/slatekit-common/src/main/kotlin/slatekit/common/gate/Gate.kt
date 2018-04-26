package slatekit.common.gate

import slatekit.common.*
import slatekit.common.results.UNEXPECTED_ERROR
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference


/**
 * Simple "circuit breaker" like component with metrics
 * This attempts to call the callback/function supplied, factoring
 * in whether or not this gate is open/closed based on errors
 *
 * Does the following
 * 1. Track number of times a call/function is invoked
 * 2. Track error ( last one )
 * 3. Track error count since last open state
 * 4. Track error threshold since last open state
 * 5. Logs on open/close
 * 6. Notifies on open/close
 */
class Gate(val name: String,
           val settings: GateSettings,
           val logger  :(String, String) -> Unit,
           val notifier:(String, String) -> Unit) : Gated {

    val status = AtomicReference<GateState>(Open)
    val statusTimeStamp = AtomicReference(DateTime.now())
    val runCount = AtomicInteger(0)
    val runSubCount = AtomicLong(0L)
    val errorCount = AtomicLong(0L)
    val error = AtomicReference<Exception>()


    /**
     * Opens the gate
     */
    override fun open() = setState(Open)


    /**
     * Closes the gate
     */
    override fun close() = setState(Closed)


    /**
     * Attempts to enter the gate
     */
    override fun <T> attempt(call: () -> T): Result<T, String> {
        return if (isOpen()) {
            try {
                val result = call()
                Success(result)
            } catch (ex: Exception) {
                errorCount.incrementAndGet()
                error.set(ex)
                Failure(ex.message ?: "", UNEXPECTED_ERROR)
            } finally {
                val count = runSubCount.incrementAndGet()

                // Up the run counts
                if (count > 1000000) {
                    runCount.incrementAndGet()
                    runSubCount.set(0)
                }

                // Check error threshold
                val totalFailed = errorCount.get().toDouble()
                val totalProcessed = runSubCount.get().toDouble()
                val percentageFailed = totalFailed / totalProcessed

                // Close!
                if (percentageFailed > settings.errorThresholdPrecentage) {
                    close()
                    logger.invoke("Gate: $name", "Closing, error threshold reached : $percentageFailed")
                    notifier.invoke("Gate: $name", "Closing, error threshold reached : $percentageFailed")
                }
            }
        } else {
            Failure("Gate closed")
        }
    }


    /**
     * Whether or not the gate is open
     */
    override fun isOpen(): Boolean = status.get() == Open


    /**
     * Whether or not the gate is closed
     */
    override fun isClosed(): Boolean = status.get() == Closed


    /**
     * Overall status of gate with metrics
     */
    override fun status(): GateMetrics {
        return GateMetrics(
                status.get(),
                statusTimeStamp.get(),
                DateTime.now(),
                runCount.get(),
                runSubCount.get(),
                errorCount.get(),
                error.get()
        )
    }


    private fun setState(state: GateState) {
        val ts = DateTime.now()
        status.set(Open)
        statusTimeStamp.set(ts)
        logger.invoke("Gate: $name", "$state at $ts")
        notifier.invoke("Gate: $name", "$state at $ts")
    }

}