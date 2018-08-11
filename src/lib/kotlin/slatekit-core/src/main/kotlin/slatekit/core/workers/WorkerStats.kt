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
package slatekit.core.workers

import slatekit.common.DateTime
import slatekit.common.ResultEx
import slatekit.common.status.RunState

data class WorkerStats(val id:String,
                       val name:String,
                       val status:RunState,
                       val lastRunTime: DateTime,
                       val lastResult:ResultEx<*>,
                       val totalRequests : Long,
                       val totalSuccesses: Long,
                       val totalErrored  : Long,
                       val totalFiltered : Long,
                       val lastRequest   : Job,
                       val lastFiltered  : Job,
                       val lastSuccess   : Pair<Job, ResultEx<*>>,
                       val lastErrored   : Pair<Job, ResultEx<*>>

)
