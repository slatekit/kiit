/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package kiit.common.log

import kiit.common.DateTime

data class LogEntry(
    val name: String = "",
    val level: LogLevel,
    val msg: String = "",
    val ex: Throwable? = null,
    val tag: String? = null,
    val time: DateTime = DateTime.now()
)