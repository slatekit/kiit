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
import kiit.results.DeniedException
import kiit.results.Err
import kiit.results.ErroredException
import kiit.results.ExceptionErr
import kiit.results.Failure
import kiit.results.IgnoredException
import kiit.results.InvalidException
import kiit.results.Result
import kiit.results.Status
import kiit.results.Success
import kiit.results.Try
import kiit.results.UnexpectedException

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
