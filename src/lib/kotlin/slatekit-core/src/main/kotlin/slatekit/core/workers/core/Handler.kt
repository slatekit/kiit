package slatekit.core.workers.core

import slatekit.common.DateTime
import slatekit.common.ResultEx
import slatekit.common.log.Logger
import slatekit.core.workers.Job
import slatekit.core.workers.Worker

/**
 * Handler class to support the result of a job having been worked on by a worker
 * You can use this to customize what do in event of a specific job result.
 * E.g.
 * 1. store the results in the database for transparency into the metrics
 * 2. integrate with some 3rd party for detailed diagnostics
 */
 open class Handler(val started: DateTime, val logger: Logger? = null) {

    open fun onRequest(job: Job, worker: Worker<*>) {
    }

    open fun onSuccess(job: Job, worker: Worker<*>, result: ResultEx<*>) {
    }

    open fun onFiltered(job: Job, worker: Worker<*>) {
    }

    open fun onErrored(job: Job, worker: Worker<*>, result: ResultEx<*>) {
    }
}
