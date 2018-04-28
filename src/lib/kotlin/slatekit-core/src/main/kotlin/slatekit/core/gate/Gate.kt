package slatekit.core.gate

import slatekit.common.*
import slatekit.common.results.UNEXPECTED_ERROR
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference


/**
 * Simple, specialized "circuit breaker" like component.
 *
 * This Gate ( like a real life security gate ) is either open / closed,
 * and permits a function call ( lambda ) to proceed if its open.
 *
 * This component has the following features:
 *
 * TRACKING:
 * 1. Tracks total requests at the gate via a count, sub-count that resets after x requests
 * 2. Tracks total errors since it was last open
 * 3. Tracks total error percentage since last open ( compared to total requests )
 * 4. Tracks last error
 * 5. Tracks last time of state change ( open / close )
 *
 * FEATURES
 * 1. Allows for x number of requests to occur before tracking of errors
 * 2. Closes the gate ( if error percentage above threshold )
 * 3. Opens the gate after a timeout period
 * 4. Supports exponential timeout / backoff if errors re-occur
 * 5. Alerts listener of gate events ( open -> close -> open )
 *
 */
open class Gate(val name: String,
           val settings: GateSettings,
           private val listener: GateListener?) : Gated {

    private val status = AtomicReference<GateState>(Open)
    private val statusTimeStamp = AtomicReference(DateTime.now())
    private val reasonForClose = AtomicReference<Reason>(NotApplicable)
    private val runCount = AtomicInteger(0)
    private val runSubCount = AtomicLong(0L)
    private val errorCount = AtomicLong(0L)
    private val error = AtomicReference<Exception>()
    private val event = AtomicReference<GateEvent>()
    private val timer = Timer()
    private val timerPosition = AtomicInteger(0)


    init {
        setState(Open)
        event.set(GateEvent(name, status.get(), reasonForClose.get(), initialMetrics()))
    }


    /**
     * Opens the gate
     */
    override fun open() {
        setState(Open)

        // Reset counters
    }


    /**
     * Closes the gate
     */
    override fun close(reason:Reason) {
        setState(Closed)

        reasonForClose.set(reason)

        // Re-open later
        scheduleReopen()

        // Bump up exponential backoff
        incrementBackOff()
    }


    /**
     * Attempts to enter the gate
     */
    override fun <T> attempt(call: () -> T): Result<T, GateEvent> {
        return if (isOpen()) {

            // Attempt
            val r1 = attemptInternal(call)

            // Failed ? Retry ?
            val finalResult = if(!r1.success && settings.retryCount > 0 ) {
                IntRange(1, settings.retryCount).fold(r1, { r, _ ->
                    if( r.success ) {
                        r
                    } else {
                        attemptInternal(call)
                    }
                })
            }
            else r1
            finalResult
        } else {
            Failure(buildGateEvent())
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
     * Overall metrics of gate
     */
    override fun metrics(): GateMetrics {
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


    /**
     * Derived classes/clients should customize the alerting for their needs
     */
    protected open fun alert() {
        val state = status.get()
        when(state) {
            is Open   -> {
                println("Gate $name is Open")
            }
            is Closed -> {
                val event = buildGateEvent()

                // Same logic for all for now ?
                // Keep as a when expression as an example
                // to show the various reasons
                when(event.reason) {
                    is ErrorsHigh   -> listener?.invoke(this, event)
                    is VolumeHigh   -> listener?.invoke(this, event)
                    is Maintainance -> listener?.invoke(this, event)
                    is ManualClose  -> listener?.invoke(this, event)
                    else            -> listener?.invoke(this, event)
                }
            }
        }
    }


    private fun <T> attemptInternal(call: () -> T): Result<T, GateEvent> {
        return try {
            val result = call()
            Success(result)
        } catch (ex: Exception) {
            errorCount.incrementAndGet()
            error.set(ex)
            Failure(buildGateEvent(), UNEXPECTED_ERROR)
        } finally {
            val count = runSubCount.incrementAndGet()

            // Up the run counts
            if (count > settings.subCountResetLimit) {
                runCount.incrementAndGet()
                runSubCount.set(0)
            }

            // Check error threshold
            val totalFailed = errorCount.get().toDouble()
            val totalProcessed = runSubCount.get().toDouble()
            val percentageFailed = totalFailed / totalProcessed

            // Close: Due to errors!
            if (percentageFailed > settings.errorThresholdPrecentage) {
                close(ErrorsHigh)
                alert()
            }
            // Close: Due to volume!
            else if(settings.volumeThresholdPerMinute > 0 && isOverLimit()) {
                close(VolumeHigh)
                alert()
            }
        }
    }


    private fun buildGateEvent(): GateEvent {
        return GateEvent(name, status.get(), reasonForClose.get(), metrics())
    }


    private fun setState(state: GateState) {
        val currentState = status.get()
        if(currentState != state) {
            val ts = DateTime.now()
            status.set(state)
            statusTimeStamp.set(ts)
            alert()
        }
    }


    private fun scheduleReopen() {
        // Schedule re-opening.
        val rawTimePos = timerPosition.get()
        val timePos = Math.min(rawTimePos, settings.reOpenTimesInSeconds.size - 1)
        val seconds = settings.reOpenTimesInSeconds[timePos]
        val task = ReOpenTask(this)
        timer.schedule(task, seconds * 1000L)
    }


    private fun incrementBackOff(){
        val rawTimePos = timerPosition.get()
        if(rawTimePos < settings.reOpenTimesInSeconds.size - 1) {
            timerPosition.set(rawTimePos + 1)
        }
    }


    private fun initialMetrics(): GateMetrics {
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


    private fun isOverLimit():Boolean {

        val totalProcessed = runSubCount.get().toDouble()
        val end = DateTime.now()
        val start = statusTimeStamp.get()
        val mins = start.raw.until(end.raw, ChronoUnit.MINUTES)
        val perMin = mins / totalProcessed
        return perMin > settings.volumeThresholdPerMinute
    }


    private fun reset() {
        reasonForClose.set(NotApplicable)
        runCount.set(0)
        runSubCount.set(0L)
        errorCount.set(0)
        error.set(null)
    }


    class ReOpenTask(private val gate:Gate) : TimerTask() {
        override fun run() {
            gate.open()
        }
    }
}