/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A Kotlin Tool-Kit for Server + Android
 * </slate_header>
 */

package slatekit.results.builders

import slatekit.results.Codes
import slatekit.results.Err
import slatekit.results.Failure
import slatekit.results.Notice
import slatekit.results.Result
import slatekit.results.Status

/**
 * Builds [Result] with [Failure] error type of [String]
 */
interface NoticeBuilder : Builder<String> {
    override fun errorFromEx(ex: Exception, defaultStatus: Status): String = ex.message ?: defaultStatus.msg
    override fun errorFromStr(msg: String?, defaultStatus: Status): String = msg ?: defaultStatus.msg
    override fun errorFromErr(err: Err, defaultStatus: Status): String = err.toString()
}

/**
 * Builds [Result] with [Failure] error type of [String]
 */
object Notices : NoticeBuilder {
    /**
     * Build a Notice<T> ( type alias ) for Result<T,String> using the supplied function
     */
    @JvmStatic
    inline fun <T> notice(f: () -> T): Notice<T> = Result.build(f, { e -> e.message ?: Codes.ERRORED.msg })
}
