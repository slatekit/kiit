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

import slatekit.results.Err
import slatekit.results.Failure
import slatekit.results.Outcome
import slatekit.results.Result
import slatekit.results.Status

/**
 * Builds [Result] with [Failure] error type of [Err]
 */
interface OutcomeBuilder : Builder<Err> {
    override fun errorFromEx(ex: Exception, defaultStatus: Status): Err = Err.of(ex)
    override fun errorFromStr(msg: String?, defaultStatus: Status): Err = Err.of(msg ?: defaultStatus.msg)
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
    inline fun <T> of(f: () -> T): Outcome<T> = Result.build(f, { ex -> Err.of(ex) })

    /**
     * Build a Outcome<T> ( type alias ) for Result<T,Err> using the value with a null check
     */
    @JvmStatic
    inline fun <T> of(t: T?): Outcome<T> = when (t) {
        null -> Outcomes.errored("null")
        else -> Outcomes.success(t)
    }

    /**
     * Build a Outcome<T> ( type alias ) for Result<T,Err> using the supplied condition
     */
    @JvmStatic
    inline fun <T> of(condition: Boolean, t: T?): Outcome<T> {
        return if (!condition)
            Outcomes.errored()
        else if (t == null)
            Outcomes.errored()
        else
            Outcomes.success(t)
    }
}
