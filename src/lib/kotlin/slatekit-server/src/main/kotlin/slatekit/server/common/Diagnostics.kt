package slatekit.server.common

import slatekit.common.requests.Request
import slatekit.common.diagnostics.Diagnostics
import slatekit.common.diagnostics.Events
import slatekit.common.log.Logger
import slatekit.common.metrics.Metrics
import slatekit.common.diagnostics.Tracker

/**
 * Standardized diagnostics using the Diagnostics component from common.
 * Provides built-in support for :
 * 1. logs
 * 2. metrics
 * 3. tracker ( last request/response )
 * 4. events ( event listneners )
 * @param metrics: Metrics to store counters/gauges/meters
 * @param logger: Logger for the API server
 */
class Diagnostics(metrics: Metrics, logger: Logger) : Diagnostics<Request>(
        // Shows up as a prefix in the logs message
        prefix = "server.apis",

        // Shows up after the log prefix ( e.g. "server.apis /api/app/accounts/register" )
        nameFetcher = { it.path },

        // Shows up in the logs as key/value pairs
        infoFetcher = { "verb:${it.verb}, version:${it.version}, tag:${it.tag}" },

        // Used as the prefix of the metric sent to capture all metrics.
        // server.apis.( total_requests | total_successes | total_failed )
        metricFetcher = { "server.apis" },

        // Used as the tags for associating metrics with
        tagsFetcher = { listOf() },

        // The logger that the diagnostics will log to
        logger = logger,

        // The metrics the diagnostics will log to
        metrics = metrics,

        // The events the diagnostics will event out to
        events = Events(),

        // The tracker the diagnostics will use for last request/response/etc
        tracker = Tracker("server.apis", "all")
)