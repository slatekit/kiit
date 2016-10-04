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

/**
 * Settings for the task. This can be subclassed
 * @param batchSize         : The number of items to process at one time ( for batchjobs )
 * @param isOngoing         : Whether this task runs continously
 * @param waitTimeInSeconds : The wait time in seconds before the task runs again after being idle
 */
case class TaskSettings(
                         batchSize             :Int      = 10,
                         isOngoing             :Boolean  = false,
                         waitTimeInSeconds     :Int      = 5,
                         pauseTimeInSeconds    :Int      = 5,
                         stopTimeInSeconds     :Int      = 30,
                         pauseAfterProcessing  :Boolean  = false
                  )
{

}
