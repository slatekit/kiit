/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A Kotlin Tool-Kit for Server + Android
  *  </kiit_header>
 */

package kiit.results.builders

import kiit.results.Codes
import kiit.results.Err
import kiit.results.Failure
import kiit.results.Notice
import kiit.results.Result
import kiit.results.Status
import kiit.results.Success

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
