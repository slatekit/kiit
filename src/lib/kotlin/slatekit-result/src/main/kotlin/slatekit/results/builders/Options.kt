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
import slatekit.results.Failed
import slatekit.results.Failure
import slatekit.results.Option
import slatekit.results.Result
import slatekit.results.Status
import slatekit.results.Success

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
