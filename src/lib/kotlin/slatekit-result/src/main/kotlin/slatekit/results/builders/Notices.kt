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
import slatekit.results.Success

/**
 * Builds [Result] with [Failure] error type of [String]
 */
interface NoticeBuilder : Builder<String> {
    override fun errorFromEx(ex: Throwable, defaultStatus: Status): String = ex.message ?: defaultStatus.desc
    override fun errorFromStr(msg: String?, defaultStatus: Status): String = msg ?: defaultStatus.desc
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
    inline fun <T> of(f: () -> T): Notice<T> = build(f, { e -> e.message ?: Codes.ERRORED.desc })

    /**
     * Build a Result<T,E> using the supplied callback and error handler
     */
    @JvmStatic
    inline fun <T> build(f: () -> T, onError: (Throwable) -> String): Notice<T> =
        try {
            val data = f()
            Success(data)
        } catch (e: Throwable) {
            Failure(onError(e))
        }
}
