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

import slatekit.results.*

/**
 * Builds [Result] with [Failure] error type of [Err]
 */
interface OutcomeBuilder : Builder<Err> {
    override fun errorFromEx(ex: Exception, defaultStatus: Status): Err = Err.ex(ex)
    override fun errorFromStr(msg: String?, defaultStatus: Status): Err = Err.of(msg ?: defaultStatus.desc)
    override fun errorFromErr(err: Err, defaultStatus: Status): Err = err
}

/**
 * Builds [Result] with [Failure] error type of [Err]
 */
object Outcomes : OutcomeBuilder {

    /**
     * Build a Outcome<T> ( type alias ) for Result<T,Err> using the supplied function
     */
    @JvmStatic
    inline fun <T> of(f: () -> T): Outcome<T> = build(f, { ex -> Err.ex(ex) })

    /**
     * Build a Result<T,E> using the supplied callback and error handler
     */
    @JvmStatic
    inline fun <T> build(f: () -> T, onError: (Exception) -> Err): Outcome<T> =
        try {
            val data = f()
            Success(data)
        } catch (e: Exception) {
            Failure(onError(e))
        }
}
