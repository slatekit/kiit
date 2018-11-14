package slatekit.core.scheduler

import slatekit.common.diagnostics.Diagnostics
import slatekit.common.diagnostics.Events
import slatekit.common.diagnostics.Tracker
import slatekit.common.log.Logger
import slatekit.common.metrics.Metrics

class Diagnostics(metrics: Metrics, logger: Logger) : Diagnostics<TaskRequest>(

        // Shows up as a prefix in the logs message
        prefix = "scheduler.tasks",

        // Shows up after the log prefix ( e.g. "scheduled.tasks cleanup" )
        nameFetcher = { it.task.id },

        // Shows up in the logs as key/value pairs
        infoFetcher = { "timestamp: ${it.timestamp}" },

        // Used as the prefix of the metric sent to capture all metrics.
        // scheduled.cleanup.( total_requests | total_successes | total_failed )
        metricFetcher = { "scheduler." + it.task.id },

        // Used as the tags for associating metrics with
        tagsFetcher = { listOf() },

        // The logger that the diagnostics will log to
        logger = logger,

        // The metrics the diagnostics will log to
        metrics = metrics,

        // The events the diagnostics will event out to
        events = Events(),

        // The tracker the diagnostics will use for last request/response/etc
        tracker = Tracker("scheduled.tasks.tracker", "all")
)