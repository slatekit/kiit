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

package slatekit.common.status

import slatekit.common.DateTime

data class RunStatus(
        val name: String = "",
        val lastRunTime: DateTime = DateTime.now(),
        val status: String = "",
        val runCount: Int = 0,
        val errorCount: Int = 0,
        val lastResult: String = ""
) {
}
