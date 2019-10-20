package slatekit.functions.middleware

import slatekit.common.metrics.Counters
import slatekit.results.Status


fun isLimited(counts: Counters, status: Status?, limit:Long):Boolean {
    return when (status) {
        null -> counts.totalProcessed() >= limit
        is Status.Succeeded -> counts.totalSucceeded() >= limit
        is Status.Denied -> counts.totalDenied() >= limit
        is Status.Invalid -> counts.totalInvalid() >= limit
        is Status.Ignored -> counts.totalIgnored() >= limit
        is Status.Errored -> counts.totalErrored() >= limit
        is Status.Unexpected -> counts.totalUnexpected() >= limit
        else -> counts.totalUnexpected() >= limit
    }
}