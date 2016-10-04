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

/**Stores the result of an execution/run of the Task
  *
  *
  * @param success           : Whether the task was successful
  * @param statusCode        : The status code
  * @param message           : A message representing task result
  * @param name              : The name of the task
  * @param totalMilliseconds : The time in milliseconds of the execution
  * @param start             : The time task started
  * @param end               : The time task ended
  * @param runCount          : The total number of times task ran
  * @param isOngoing         : Whether this is an continously running task
  * @param result            : The result of the task
  *
  * @note : If this is an ongoing task that runs multiple times, then the
  *         the start, end, runcount, and totalmilliseconds represent the
  *         times of the last run
 */
case class TaskResult (
                          success           :Boolean  = false         ,
                          statusCode        :Int      = 0             ,
                          message           :String   = ""            ,
                          name              :String   = ""            ,
                          totalMilliseconds :Int      = 0             ,
                          start             :DateTime = DateTime.now(),
                          end               :DateTime = DateTime.now(),
                          runCount          :Int      = 0             ,
                          isOngoing         :Boolean  = false         ,
                          result            :Any      = null
                      )
{
}