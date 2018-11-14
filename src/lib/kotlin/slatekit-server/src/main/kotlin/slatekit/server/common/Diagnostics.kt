package slatekit.server.common

import slatekit.common.Request
import slatekit.common.diagnostics.Diagnostics
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
        prefix = "server.apis",
        nameFetcher = { it.path },
        infoFetcher = { "verb:${it.verb}, version:${it.version}, tag:${it.tag}" },
        metricFetcher = { "server.apis" },
        tagsFetcher = { listOf() },
        logger = logger,
        metrics = metrics,
        events = slatekit.common.diagnostics.Events(),
        tracker = Tracker("server.apis", "all")
)