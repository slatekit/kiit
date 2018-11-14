package slatekit.workers

import slatekit.common.diagnostics.Diagnostics
import slatekit.common.diagnostics.Tracker
import slatekit.common.log.Logger
import slatekit.common.metrics.Metrics



/**
 * Standardized diagnostics using the Diagnostics component from common.
 * Provides built-in support for :
 * 1. logs
 * 2. metrics
 * 3. tracker ( last request/response )
 * 4. events ( event listeners )
 * @param metrics: Metrics to store counters/gauges/meters
 * @param logger: Logger for the API server
 */
class Diagnostics(prefix:String, metrics: Metrics, logger: Logger) : Diagnostics<WorkRequest>(
        prefix = prefix,
        nameFetcher = { it.job.task },
        infoFetcher = { "id:${it.job.id}, queue:${it.queue.name}, worker:${it.worker.id}, priority:${it.queue.priority.name}, ref_id:${it.job.refId}" },
        metricFetcher = { prefix },
        tagsFetcher = { listOf() },
        logger = logger,
        metrics = metrics,
        events = slatekit.common.diagnostics.Events(),
        tracker = Tracker(prefix, "all")
)