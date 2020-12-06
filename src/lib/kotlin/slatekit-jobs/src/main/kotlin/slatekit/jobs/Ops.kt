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
 *
 * NOTES:
 * 1. When called on the Jobs component, the name can be "ALL" to perform the operation on all jobs
 * 2. When called on the Jobs component, the name can be either job "sign.emails" or worker "signup.email.worker1"
 * 3. When called on the Job  component, the name can be "ALL" to perform the operation on all workers
 * 4. When called on the Job  component, the name can be just the name of the worker "worker1"
 */
interface Ops<T> {
    /**
     * Gets the job by name e.g. "area.service"
     */
    operator fun get(name: String): T?

    /**
     * starts the job and/or worker
     * 1. Job    = {Identity.area}.{Identity.service}
     * 2. Worker = {Identity.area}.{Identity.service}.{Identity.instance}
     * @param name: The name of the job/worker
     * @sample : job = "signup.emails", worker = "signup.emails.worker_1"
     */
    suspend fun start(name: String = Jobs.ALL): Outcome<String> = perform(name, Action.Start)

    /**
     * pauses the job and/or worker
     * 1. Job    = {Identity.area}.{Identity.service}
     * 2. Worker = {Identity.area}.{Identity.service}.{Identity.instance}
     * @param name: The name of the job/worker
     * @sample : job = "signup.emails", worker = "signup.emails.worker_1"
     */
    suspend fun pause(name: String = Jobs.ALL): Outcome<String> = perform(name, Action.Pause)

    /**
     * resumes the job and/or worker
     * 1. Job    = {Identity.area}.{Identity.service}
     * 2. Worker = {Identity.area}.{Identity.service}.{Identity.instance}
     * @param name: The name of the job/worker
     * @sample : job = "signup.emails", worker = "signup.emails.worker_1"
     */
    suspend fun resume(name: String = Jobs.ALL): Outcome<String> = perform(name, Action.Resume)

    /**
     * stops the job and/or worker
     * 1. Job    = {Identity.area}.{Identity.service}
     * 2. Worker = {Identity.area}.{Identity.service}.{Identity.instance}
     * @param name: The name of the job/worker
     * @sample : job = "signup.emails", worker = "signup.emails.worker_1"
     */
    suspend fun delay(name: String = Jobs.ALL, seconds: Int? = null): Outcome<String> = perform(name, Action.Delay, seconds)

    /**
     * checks the job and/or worker
     * 1. Job    = {Identity.area}.{Identity.service}
     * 2. Worker = {Identity.area}.{Identity.service}.{Identity.instance}
     * @param name: The name of the job/worker
     * @sample : job = "signup.emails", worker = "signup.emails.worker_1"
     */
    suspend fun check(name: String = Jobs.ALL): Outcome<String> = perform(name, Action.Check)

    /**
     * checks the job and/or worker
     * 1. Job    = {Identity.area}.{Identity.service}
     * 2. Worker = {Identity.area}.{Identity.service}.{Identity.instance}
     * @param name: The name of the job/worker
     * @sample : job = "signup.emails", worker = "signup.emails.worker_1"
     */
    suspend fun process(name: String = Jobs.ALL): Outcome<String> = perform(name, Action.Process)

    /**
     * stops the job and/or worker
     * 1. Job    = {Identity.area}.{Identity.service}
     * 2. Worker = {Identity.area}.{Identity.service}.{Identity.instance}
     * @param name: The name of the job/worker
     * @sample : job = "signup.emails", worker = "signup.emails.worker_1"
     */
    suspend fun stop(name: String = Jobs.ALL): Outcome<String> = perform(name, Action.Stop)

    /**
     * performs the operation on the supplied job/worker
     * 1. Job    = {Identity.area}.{Identity.service}
     * 2. Worker = {Identity.area}.{Identity.service}.{Identity.instance}
     * @param name: The name of the job/worker
     * @sample : job = "signup.emails", worker = "signup.emails.worker_1"
     */
    suspend fun perform(name: String = Jobs.ALL, action: Action, seconds:Int? = null): Outcome<String>
}
