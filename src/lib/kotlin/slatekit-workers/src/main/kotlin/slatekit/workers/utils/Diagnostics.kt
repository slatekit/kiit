package slatekit.workers.utils

import slatekit.common.diagnostics.Diagnostics
import slatekit.common.diagnostics.Events
import slatekit.common.diagnostics.Tracker
import slatekit.common.log.Logger
import slatekit.common.metrics.Metrics
import slatekit.workers.WorkRequest


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
        // Shows up as a prefix in the logs message
        prefix = "workers.jobs",

        // Shows up after the log prefix ( e.g. "workers.jobs task1" )
        nameFetcher = { it.job.task },

        // Shows up in the logs as key/value pairs
        infoFetcher = { "id:${it.job.id}, queue:${it.queue.name}, worker:${it.worker.id}, priority:${it.queue.priority.name}, ref_id:${it.job.refId}" },

        // Used as the prefix of the metric sent to capture all metrics.
        // workers.apis.( total_requests | total_successes | total_failed )
        metricFetcher = { "workers." + it.worker.metricId },

        // Used as the tags for associating metrics with
        tagsFetcher = { listOf() },

        // The logger that the diagnostics will log to
        logger = logger,

        // The metrics the diagnostics will log to
        metrics = metrics,

        // The events the diagnostics will event out to
        events = Events(),

        // The tracker the diagnostics will use for last request/response/etc
        tracker = Tracker("workers.jobs", "all")
)