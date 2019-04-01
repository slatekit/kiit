/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * philosophy: Simplicity above all else
 * </slate_header>
 */

package slatekit.results.builders

import slatekit.results.*
import slatekit.results.StatusGroup


/**
 * Builds [Result] with [Failure] error type of [String]
 */
interface NoticeBuilder: Builder<String> {
    override fun errorFromEx(ex: Exception, defaultStatus: StatusGroup): String = ex.message ?: defaultStatus.msg
    override fun errorFromStr(msg: String?, defaultStatus: StatusGroup):String = msg ?: defaultStatus.msg
    override fun errorFromErr(err: Err, defaultStatus: StatusGroup): String = err.toString()
}


/**
 * Builds [Result] with [Failure] error type of [String]
 */
object Notices: NoticeBuilder