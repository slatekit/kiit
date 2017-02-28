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
                          success           :Boolean ,
                          statusCode        :Int     ,
                          message           :String  ,
                          name              :String  ,
                          totalMilliseconds :Int     ,
                          start             :DateTime,
                          end               :DateTime,
                          runCount          :Int     ,
                          isOngoing         :Boolean ,
                          result            :Any
                      )
{
}