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

package slatekit.common.info

import slatekit.common.DateTime
import slatekit.common.TimeSpan
import java.time.Duration

data class Status(
        val started: DateTime = DateTime.now(),
        val ended: DateTime = DateTime.now(),
        val duration: Duration = started.durationFrom(ended),
        val status: String = "not-started",
        val errors: Int = 0,
        val error: String = "n/a"
) {
    fun start(statusName: String? = null): Status =
            copy(started = DateTime.now(), status = statusName ?: "started")


    fun error(msg: String): Status = copy(error = msg, errors = errors + 1)


    fun end(statusName: String? = null): Status {
        val now = DateTime.now()
        val duration = started.durationFrom(now)
        return copy(ended = DateTime.now(), status = statusName ?: "ended", duration = duration)
    }


    companion object {
        @JvmStatic
        val none = Status()
    }
}