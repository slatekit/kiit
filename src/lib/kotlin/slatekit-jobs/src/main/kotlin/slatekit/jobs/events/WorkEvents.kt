package slatekit.jobs.events

import slatekit.functions.policy.Policy
import slatekit.jobs.WorkRequest
import slatekit.jobs.WorkResult
import slatekit.jobs.Worker

interface WorkEvents : Events<Worker<*>> {

    /**
     * Applies the policy to these workers
     * Policies add behaviour to workers such as retries, limits, error ratios, etc
     */
    fun apply(policy: Policy<WorkRequest, WorkResult>)
}