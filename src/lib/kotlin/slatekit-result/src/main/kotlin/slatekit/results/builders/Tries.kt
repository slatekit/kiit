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
 * Builds [Result] with [Failure] error type of [Exception]
 */
interface TryBuilder : Builder<Exception> {
    override fun errorFromEx(ex: Exception, defaultStatus: StatusGroup): Exception = ex
    override fun errorFromStr(msg: String?, defaultStatus: StatusGroup): Exception = Exception(msg ?: defaultStatus.msg)
    override fun errorFromErr(err: Err, defaultStatus: StatusGroup): Exception = ExceptionWithErr(defaultStatus.msg, err)
}


/**
 * Builds [Result] with [Failure] error type of [Exception]
 */
object Tries : TryBuilder
