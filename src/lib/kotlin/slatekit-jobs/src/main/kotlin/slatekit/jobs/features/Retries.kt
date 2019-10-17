package slatekit.jobs.features

import slatekit.jobs.support.retry



/**
 * Rule to control the maximum number of items a worker can process
 */
class Retries(val limit: Long) : Handler(false, { state, worker, task ->
    retry(limit.toInt()) {
        worker.work(task)
    }
})