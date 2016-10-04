/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2016 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
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
