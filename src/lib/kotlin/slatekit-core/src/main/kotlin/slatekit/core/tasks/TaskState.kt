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

import slatekit.common.DateTime

/**
 * Keeps track of the state of the running task.
 * @param name        : Name of the task
 * @param lastRunTime : The last time it ran
 * @param hasRun      : Whether this ran at least 1 time
 * @param runCount    : The number of times the task ran
 * @param errorCount  : The total number of errors
 * @param lastResult  : The last task result
 */
data class TaskState(
        val name: String,
        val lastRunTime: DateTime,
        val hasRun: Boolean,
        val runCount: Int,
        val errorCount: Int,
        val lastResult: String
) {
}
