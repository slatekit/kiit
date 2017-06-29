/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.core.tasks

/**
 * Settings for the task. This can be subclassed
 * @param batchSize         : The number of items to process at one time ( for batchjobs )
 * @param isOngoing         : Whether this task runs continously
 * @param waitTimeInSeconds : The wait time in seconds before the task runs again after being idle
 */
data class TaskSettings(
        val batchSize: Int = 10,
        val isOngoing: Boolean = false,
        val waitTimeInSeconds: Int = 5,
        val pauseTimeInSeconds: Int = 5,
        val stopTimeInSeconds: Int = 30
) {

}
