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

package slatekit.common.log

import slatekit.common.DateTime

data class LogEntry(
    val name: String = "",
    val level: LogLevel,
    val msg: String = "",
    val ex: Exception? = null,
    val tag: String? = null,
    val time: DateTime = DateTime.now()
)