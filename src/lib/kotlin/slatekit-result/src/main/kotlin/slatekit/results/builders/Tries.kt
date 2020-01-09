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
import slatekit.results.StatusException
import slatekit.results.Success
import slatekit.results.Try
import slatekit.results.UnexpectedException

/**
 * Builds [Result] with [Failure] error type of [Exception]
 */
interface TryBuilder : Builder<Exception> {
    override fun errorFromEx(ex: Exception, defaultStatus: Status): Exception = ex
    override fun errorFromStr(msg: String?, defaultStatus: Status): Exception = Exception(msg ?: defaultStatus.msg)
    override fun errorFromErr(err: Err, defaultStatus: Status): Exception = ExceptionErr(defaultStatus.msg, err)
}

/**
 * Builds [Result] with [Failure] error type of [Exception]
 */
object Tries : TryBuilder {

    /**
     * Build a Try<T> ( Result<T,Exception> ) using the supplied callback.
     * This allows for using throw [Exception] to build the Try
     * by getting the appropriate status code out of the defined exception
     */
    @JvmStatic
    inline fun <T> of(f: () -> T): Try<T> =
        try {
            val data = f()
            Success(data)
        } catch (e: DeniedException) {
            Failure(e, Status.ofStatus(e.msg, e.status, Codes.DENIED))
        } catch (e: IgnoredException) {
            Failure(e, Status.ofStatus(e.msg, e.status, Codes.IGNORED))
        } catch (e: InvalidException) {
            Failure(e, Status.ofStatus(e.msg, e.status, Codes.INVALID))
        } catch (e: ErroredException) {
            Failure(e, Status.ofStatus(e.msg, e.status, Codes.ERRORED))
        } catch (e: UnexpectedException) {
            // Theoretically, anything outside of Denied/Ignored/Invalid/Errored
            // is an unexpected expection ( even a normal [Exception].
            // However, this is here for completeness ( to have exceptions
            // that correspond to the various [Status] groups), and to cover the
            // case when someone wants to explicitly use an UnhandledException
            // or Status group/code
            Failure(e, Status.ofStatus(e.message, null, Codes.UNEXPECTED))
        } catch (e: Exception) {
            when (e) {
                is StatusException -> Failure(e, Status.ofStatus(e.msg, e.status, Codes.UNEXPECTED))
                else -> Failure(e, Status.ofStatus(e.message, null, Codes.UNEXPECTED))
            }
        }
}
