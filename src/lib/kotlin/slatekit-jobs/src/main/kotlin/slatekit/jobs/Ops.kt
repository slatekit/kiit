package slatekit.jobs

import slatekit.common.Identity
import slatekit.jobs.Action
import slatekit.jobs.support.Command
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes

/**
 * This interface is reused by both the Jobs component and the Job component.
 * 1. Jobs is a collection of all jobs
 * 2. Job represents 1 job
 *
 * The structure of the component is
 * Jobs
 *      - Job 1 ( send welcome emails : "signup.emails" )
 *              - Workers
 *                  - Worker 1
 *                  - Worker 2
 *
 *      - Job 2 ( send sms alerts     : "signup.alerts" )
 *              - Workers
 *                  - Worker 3
 *                  - Worker 4
 * starts a single item in this component
 * 1. Job    = {Identity.area}.{Identity.service}
 * 2. Worker = {Identity.area}.{Identity.service}.{Identity.instance}
 */
interface Ops<T> {
    /**
     * Gets the item by name e.g.
     * job name = "area.service"
     * worker name = "area.service.instance"
     */
    operator fun get(name: String): T?

    suspend fun delay(): Outcome<String> = send(Action.Delay)
    suspend fun delay(name: String): Outcome<String> = send(name, Action.Delay, "")

    suspend fun start(): Outcome<String> = send(Action.Start)
    suspend fun start(name: String): Outcome<String> = send(name, Action.Start, "")

    suspend fun pause(): Outcome<String> = send(Action.Pause)
    suspend fun pause(name: String): Outcome<String> = send(name, Action.Pause, "")

    suspend fun resume(): Outcome<String> = send(Action.Resume)
    suspend fun resume(name: String): Outcome<String> = send(name, Action.Resume, "")

    suspend fun check(): Outcome<String> = send(Action.Check)
    suspend fun check(name: String): Outcome<String> = send(name, Action.Check, "")

    suspend fun process(): Outcome<String> = send(Action.Process)
    suspend fun process(name: String): Outcome<String> = send(name, Action.Process, "")

    suspend fun stop(): Outcome<String> = send(Action.Stop)
    suspend fun stop(name: String): Outcome<String> = send(name, Action.Stop, "")

    suspend fun kill(): Outcome<String> = send(Action.Kill)
    suspend fun kill(name: String): Outcome<String> = send(name, Action.Kill, "")

    /**
     * Sends a command to take action ( start, pause, stop, etc ) on all items
     */
    suspend fun send(action: Action): Outcome<String>

    /**
     * Sends a command to take action ( start, pause, stop, etc ) on a specific job or worker by id
     */
    suspend fun send(id: Identity, action: Action, note: String): Outcome<String>

    /**
     * Sends a command to take action ( start, pause, stop, etc ) on a specific job or worker by id
     */
    suspend fun send(name:String, action: Action, note: String): Outcome<String> {
        return when(val id = toId(name)) {
            null -> Outcomes.invalid("Unable to find job or worker with name $name")
            else -> send(id, action, note)
        }
    }

    /**
     * Requests this job to perform the supplied command
     * Coordinator handles requests via kotlin channels
     */
    suspend fun send(command: Command): Outcome<String>

    /**
     * Converts the name to an identity
     */
    fun toId(name: String): Identity?
}
