package slatekit.jobs

import slatekit.results.Outcome

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

    suspend fun start(): Outcome<String> = start(Request(Jobs.ALL))
    suspend fun start(name: String): Outcome<String> = start(Request(name))
    suspend fun start(request: Request): Outcome<String> = perform(request, Action.Start)

    suspend fun pause(): Outcome<String> = pause(Request(Jobs.ALL))
    suspend fun pause(name: String): Outcome<String> = pause(Request(name))
    suspend fun pause(request: Request): Outcome<String> = perform(request, Action.Pause)

    suspend fun resume(): Outcome<String> = resume(Request(Jobs.ALL))
    suspend fun resume(name: String): Outcome<String> = resume(Request(name))
    suspend fun resume(request: Request): Outcome<String> = perform(request, Action.Resume)

    suspend fun delay(seconds: Int? = null): Outcome<String> = delay(Request(Jobs.ALL, seconds))
    suspend fun delay(name: String, seconds: Int? = null): Outcome<String> = delay(Request(name, seconds))
    suspend fun delay(request: Request): Outcome<String> = perform(request, Action.Delay)

    suspend fun check(): Outcome<String> = check(Request(Jobs.ALL))
    suspend fun check(name: String): Outcome<String> = check(Request(name))
    suspend fun check(request: Request): Outcome<String> = perform(request, Action.Check)

    suspend fun process(): Outcome<String> = process(Request(Jobs.ALL))
    suspend fun process(name: String): Outcome<String> = process(Request(name))
    suspend fun process(request: Request): Outcome<String> = perform(request, Action.Process)

    suspend fun stop(): Outcome<String> = stop(Request(Jobs.ALL))
    suspend fun stop(name: String): Outcome<String> = stop(Request(name))
    suspend fun stop(request: Request): Outcome<String> = perform(request, Action.Stop)

    /**
     * performs the operation on the supplied job/worker
     * 1. Job    = {Identity.area}.{Identity.service}
     * 2. Worker = {Identity.area}.{Identity.service}.{Identity.instance}
     * @param name: The name of the job/worker
     * @sample : job = "signup.emails", worker = "signup.emails.worker_1"
     */
    suspend fun perform(request:Request, action: Action): Outcome<String>
}
