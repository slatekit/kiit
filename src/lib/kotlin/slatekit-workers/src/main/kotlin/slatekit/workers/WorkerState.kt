/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
package slatekit.workers

import slatekit.common.DateTime
import slatekit.common.requests.Response
import slatekit.common.Status
import slatekit.common.functions.FunctionInfo
import slatekit.common.functions.FunctionMode
import slatekit.common.functions.FunctionState
import slatekit.common.metrics.Metrics
import slatekit.results.Try
import slatekit.workers.slatekit.workers.JobResult

/**
 * Worker level status that can be supplied to a front-end
 * and / or for logging/diagnostics purposes.
 */
data class WorkerState(
        val id: String,
        val name: String,
        val status: Status,
        val lastRunTime: DateTime,
        val lastResult: Try<*>,
        val totals:List<Pair<String,Long>>,
        val lastRequest: WorkRequest?,
        val lastFiltered: WorkRequest?,
        val lastSuccess: Pair<WorkRequest, Response<*>>?,
        val lastErrored: Pair<WorkRequest, Exception?>?
)


data class WorkerState2(
        val totals:List<Pair<String,Long>>,
        val lastRequest: WorkRequest?,
        val lastFiltered: WorkRequest?,
        val lastSuccess: Pair<WorkRequest, Response<*>>?,
        val lastErrored: Pair<WorkRequest, Exception?>?,
        override val info: FunctionInfo,
        override val status: Status,
        override val msg: String,
        override val lastRun: DateTime,
        override val lastMode: FunctionMode,
        override val hasRun: Boolean,
        override val metrics: Metrics,
        override val lastResult: JobResult
) : FunctionState<JobResult>