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
import kiit.results.Failed
import kiit.results.Failure
import kiit.results.Option
import kiit.results.Result
import kiit.results.Status
import kiit.results.Success

/**
 * Builds [Result] with [Failure] error type of [Unit]
 */
interface OptionsBuilder : Builder<Unit> {
    override fun errorFromEx(ex: Throwable, defaultStatus: Status): Unit = Unit
    override fun errorFromStr(msg: String?, defaultStatus: Status): Unit = Unit
    override fun errorFromErr(err: Err, defaultStatus: Status): Unit = Unit
}

/**
 * Builds [Result] with [Failure] error type of [String]
 */
object Options : OptionsBuilder {
    /**
     * Build a Notice<T> ( type alias ) for Result<T,String> using the supplied function
     */
    @JvmStatic
    inline fun <T> of(f: () -> T): Option<T> = build(f, { _ -> Codes.ERRORED })

    /**
     * Build a Result<T,E> using the supplied callback and error handler
     */
    @JvmStatic
    inline fun <T> build(f: () -> T, onError: (Throwable) -> Failed): Option<T> =
        try {
            val data = f()
            Success(data)
        } catch (e: Throwable) {
            val status = onError(e)
            Failure(Unit, status)
        }
}
