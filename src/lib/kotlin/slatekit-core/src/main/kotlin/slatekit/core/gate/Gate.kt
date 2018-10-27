package slatekit.core.gate

import slatekit.common.*
import slatekit.common.results.ResultCode.UNEXPECTED_ERROR
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
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
open class Gate(
    val name: String,
    val settings: GateSettings,
    private val listener: GateListener?
) : Gated {

    private val status = AtomicReference<GateState>(Open)
    private val statusTimeStamp = AtomicReference(DateTime.now())
    private val reasonForClose = AtomicReference<Reason>(NotApplicable)
    private val volumeLimiter = Tracker()
    private val errorLimiter = ErrorTracker()
    private val event = AtomicReference<GateEvent>()
    private val timer = Timer()
    private val timerPosition = AtomicInteger(0)
    private val gateStates = mutableListOf<GateStatus>()

    init {
        setState(Open)
        val initialMetrics = internalMetrics()
        gateStates.add(initialMetrics)
        event.set(GateEvent(name, status.get(), reasonForClose.get(), initialMetrics))
    }

    /**
     * Opens the gate
     */
    override fun open(alert: Boolean) {

        // Keep last state changes
        // Use queue later
        gateStates.add(0, metrics())

        setState(Open)

        // Reset counters
        reset()

        if (alert) {
            alert()
        }
    }

    /**
     * Closes the gate
     */
    override fun close(reason: Reason, alert: Boolean) {
        setState(Closed)

        reasonForClose.set(reason)

        if (settings.reOpenAutomatically) {
            // Re-open later
            scheduleReopen()

            // Bump up exponential backoff
            incrementBackOff()
        }

        if (alert) {
            alert()
        }
    }

    /**
     * Attempts to enter the gate
     */
    override fun <T> attempt(call: () -> T): Result<T, GateEvent> {
        return if (isOpen()) {

            // Attempt
            val r1 = attemptInternal(call)

            // Failed ? Retry ?
            val finalResult = if (!r1.success && settings.retryCount > 0) {
                IntRange(1, settings.retryCount).fold(r1, { r, _ ->
                    if (r.success) {
                        r
                    } else {
                        attemptInternal(call)
                    }
                })
            } else r1
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
    override fun metrics(): GateStatus = internalMetrics()

    override fun states(): List<GateStatus> = gateStates.toList()

    /**
     * Schedule reopening of this task after the seconds supplied
     */
    override fun openLater(seconds: Int, alert: Boolean) {
        val task = ReOpenTask(this, alert)
        timer.schedule(task, seconds * 1000L)
    }

    /**
     * Derived classes/clients should customize the alerting for their needs
     */
    protected open fun alert() {
        val state = status.get()
        when (state) {
            is Open -> {
                println("Gate $name is Open")
            }
            is Closed -> {
                val event = buildGateEvent()

                // Same logic for all for now ?
                // Keep as a when expression as an example
                // to show the various reasons
                when (event.reason) {
                    is ErrorsHigh -> listener?.invoke(this, event)
                    is VolumeHigh -> listener?.invoke(this, event)
                    is Maintainance -> listener?.invoke(this, event)
                    is ManualClose -> listener?.invoke(this, event)
                    else -> listener?.invoke(this, event)
                }
            }
        }
    }

    private fun <T> attemptInternal(call: () -> T): Result<T, GateEvent> {
        return try {
            val result = call()
            Success(result)
        } catch (ex: Exception) {
            errorLimiter.inc(ex)
            Failure(buildGateEvent(), UNEXPECTED_ERROR)
        } finally {
            val count = volumeLimiter.inc()

            // Close: Due to errors!
            if (isHighErrorCount()) {
                close(ErrorsHigh, alert = true)
            }

            // Close: Due to volume!
            else if (isHighVolume()) {
                close(VolumeHigh, alert = true)
            }

            // Up the run counts
            if (count >= settings.subCountResetLimit) {
                volumeLimiter.next()
                errorLimiter.next()
            }
        }
    }

    private fun buildGateEvent(): GateEvent {
        return GateEvent(name, status.get(), reasonForClose.get(), metrics())
    }

    private fun setState(state: GateState) {
        val currentState = status.get()
        if (currentState != state) {
            val ts = DateTime.now()
            status.set(state)
            statusTimeStamp.set(ts)
        }
    }

    private fun scheduleReopen() {
        // Schedule re-opening.
        val rawTimePos = timerPosition.get()
        val timePos = Math.min(rawTimePos, settings.reOpenTimesInSeconds.size - 1)
        val seconds = settings.reOpenTimesInSeconds[timePos]
        openLater(seconds, true)
    }

    private fun incrementBackOff() {
        val rawTimePos = timerPosition.get()
        if (rawTimePos < settings.reOpenTimesInSeconds.size - 1) {
            timerPosition.set(rawTimePos + 1)
        }
    }

    private fun internalMetrics(): GateStatus {
        return GateStatus(
                status.get(),
                reasonForClose.get(),
                statusTimeStamp.get(),
                DateTime.now(),
                volumeLimiter.batch.get(),
                volumeLimiter.count.get(),
                volumeLimiter.total.get(),
                errorLimiter.count.get(),
                errorLimiter.total.get(),
                errorLimiter.err.get()
        )
    }

    private fun isHighVolume(): Boolean {
        return if (settings.volumeThresholdPerMinute > 0) {
            val batchProcessed = volumeLimiter.count.get().toDouble()
            val end = DateTime.now()
            val start = statusTimeStamp.get()
            val mins = start.raw.until(end.raw, ChronoUnit.MINUTES)
            val perMin = mins / batchProcessed
            perMin > settings.volumeThresholdPerMinute
        } else {
            false
        }
    }

    private fun isHighErrorCount(): Boolean {

        // Check error threshold
        val batchProcessed = volumeLimiter.count.get().toDouble()
        return if (batchProcessed > 0) {
            val totalFailed = errorLimiter.count.get().toDouble()
            val percentageFailed = totalFailed / batchProcessed
            percentageFailed > settings.errorThresholdPercentage
        } else {
            false
        }
    }

    private fun reset() {
        reasonForClose.set(NotApplicable)
        volumeLimiter.reset()
        errorLimiter.reset()

        // only keep last 10 states
        if (gateStates.size > 20) {
            (gateStates.size - 1..10).forEach { ndx ->
                if (ndx <= gateStates.size) {
                    gateStates.removeAt(ndx)
                }
            }
        }
    }

    class ReOpenTask(private val gate: Gate, private val alert: Boolean) : TimerTask() {
        override fun run() {
            gate.open(alert)
        }
    }
}