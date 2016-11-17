/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
</slate_header>
  */

package slate.core.tasks

import slate.common.DateTime

/**
 * Keeps track of the state of the running task.
 * @param name        : Name of the task
 * @param lastRunTime : The last time it ran
 * @param hasRun      : Whether this ran at least 1 time
 * @param runCount    : The number of times the task ran
 * @param errorCount  : The total number of errors
 * @param lastResult  : The last task result
 */
case class TaskState(
                        name          : String   = ""             ,
                        lastRunTime   : DateTime = DateTime.now() ,
                        hasRun        : Boolean  = false          ,
                        runCount      : Int      = 0              ,
                        errorCount    : Int      = 0              ,
                        lastResult    : String   = null
                    )
{
}
