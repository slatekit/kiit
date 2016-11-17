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
