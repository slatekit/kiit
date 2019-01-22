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
package slatekit.workers.core

import slatekit.common.DateTime
import slatekit.common.requests.Response
import slatekit.common.ResultEx
import slatekit.common.status.Status
import slatekit.workers.WorkRequest

/**
 * Worker level status that can be supplied to a front-end
 * and / or for logging/diagnostics purposes.
 */
data class Stats(
        val id: String,
        val name: String,
        val status: Status,
        val lastRunTime: DateTime,
        val lastResult: ResultEx<*>,
        val totalRequests: Long,
        val totalSuccesses: Long,
        val totalErrored: Long,
        val totalFiltered: Long,
        val lastRequest: WorkRequest?,
        val lastFiltered: WorkRequest?,
        val lastSuccess: Pair<WorkRequest, Response<*>>?,
        val lastErrored: Pair<WorkRequest, Exception?>?

)
