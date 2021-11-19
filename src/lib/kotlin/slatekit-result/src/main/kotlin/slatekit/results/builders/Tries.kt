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
import slatekit.results.DeniedException
import slatekit.results.Err
import slatekit.results.ErroredException
import slatekit.results.ExceptionErr
import slatekit.results.Failure
import slatekit.results.IgnoredException
import slatekit.results.InvalidException
import slatekit.results.Result
import slatekit.results.Status
import slatekit.results.Success
import slatekit.results.Try
import slatekit.results.UnexpectedException

/**
 * Builds [Result] with [Failure] error type of [Exception]
 */
interface TryBuilder : Builder<Throwable> {
    override fun errorFromEx(ex: Throwable, defaultStatus: Status): Throwable = ex
    override fun errorFromStr(msg: String?, defaultStatus: Status): Throwable = Throwable(msg ?: defaultStatus.desc)
    override fun errorFromErr(err: Err, defaultStatus: Status): Throwable = ExceptionErr(defaultStatus.desc, err)
}

/**
 * Builds [Result] with [Failure] error type of [Throwable]
 */
object Tries : TryBuilder {

    /**
     * Build a Try<T> ( Result<T,Throwable> ) using the supplied callback.
     * This allows for using throw [Throwable] to build the Try
     * by getting the appropriate status code out of the defined exception
     */
    @JvmStatic
    inline fun <T> of(f: () -> T): Try<T> =
        try {
            val data = f()
            Success(data)
        } catch (e: DeniedException) {
            Tries.denied(e, Status.ofStatus(e.msg, e.status, Codes.DENIED))
        } catch (e: IgnoredException) {
            Tries.ignored(e, Status.ofStatus(e.msg, e.status, Codes.IGNORED))
        } catch (e: InvalidException) {
            Tries.invalid(e, Status.ofStatus(e.msg, e.status, Codes.INVALID))
        } catch (e: ErroredException) {
            Tries.errored(e, Status.ofStatus(e.msg, e.status, Codes.ERRORED))
        } catch (e: UnexpectedException) {
            // Theoretically, anything outside of Denied/Ignored/Invalid/Errored
            // is an unexpected expection ( even a normal [Throwable].
            // However, this is here for completeness ( to have exceptions
            // that correspond to the various [Status] groups), and to cover the
            // case when someone wants to explicitly use an UnhandledException
            // or Status group/code
            Tries.unexpected(e, Status.ofStatus(e.message, null, Codes.UNEXPECTED))
        } catch (e: Throwable) {
            Tries.unexpected(e)
        }
}
