package slatekit.jobs.events

import slatekit.common.Status
import slatekit.functions.policy.Policy
import slatekit.jobs.WorkRequest
import slatekit.jobs.WorkResult
import slatekit.jobs.Worker


interface Events<T> {

    /**
     * Subscribes clients to a change of @see[slatekit.common.Status] on any item in this component
     * @param op:Operation to call when status is changed
     */
    suspend fun subscribe(op:suspend (T) -> Unit )


    /**
     * Subscribes clients to a change to the specific status @see[slatekit.common.Status]
     * @param op:Operation to call when status is changed
     */
    suspend fun subscribe(status: Status, op:suspend (T) -> Unit )
}



interface WorkEvents : Events<Worker<*>> {

    /**
     * Applies the policy to these workers
     * Policies add behaviour to workers such as retries, limits, error ratios, etc
     */
    fun apply(policy: Policy<WorkRequest, WorkResult>)
}