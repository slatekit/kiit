package slatekit.jobs.events

import slatekit.functions.policy.Policy
import slatekit.jobs.*

class WorkerEvents(val workers: Workers) : SubscribedEvents<Worker<*>>(), WorkEvents {
    val policies = mutableListOf<Policy<WorkRequest, WorkState>>()

    override suspend fun notify(item: Worker<*>) {
        notify(item, item.status())
    }


    /**
     * Applies the policy to these workers
     * Policies add behaviour to workers such as retries, limits, error ratios, etc
     */
    override fun apply(policy: Policy<WorkRequest, WorkState>) {
        policies.add(policy)
    }
}