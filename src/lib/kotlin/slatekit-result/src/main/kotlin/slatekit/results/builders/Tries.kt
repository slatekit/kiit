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

import slatekit.results.*


/**
 * Builds [Result] with [Failure] error type of [Exception]
 */
interface TryBuilder : Builder<Exception> {
    override fun errorFromEx(ex: Exception, defaultStatus: Status): Exception = ex
    override fun errorFromStr(msg: String?, defaultStatus: Status): Exception = Exception(msg ?: defaultStatus.msg)
    override fun errorFromErr(err: Err, defaultStatus: Status): Exception = ExceptionWithErr(defaultStatus.msg, err)
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
    inline fun <T> attempt(f: () -> T): Try<T> = attemptWithStatus {
        val data = f()
        Success(data)
    }

    /**
     * Build a Try<T> ( Result<T,Exception> ) using the supplied callback.
     * This allows for using throw [Exception] to build the Try
     * by getting the appropriate status code out of the defined exception
     */
    @JvmStatic
    inline fun <T> attemptWithStatus(f: () -> Success<T>): Try<T> =
            try {
                val data = f()
                data
            } catch (e: DeniedException) {
                Failure(e, Result.build(e.msg, e.status, StatusCodes.DENIED))
            } catch (e: IgnoredException) {
                Failure(e, Result.build(e.msg, e.status, StatusCodes.IGNORED))
            } catch (e: InvalidException) {
                Failure(e, Result.build(e.msg, e.status, StatusCodes.INVALID))
            } catch (e: ErroredException) {
                Failure(e, Result.build(e.msg, e.status, StatusCodes.ERRORED))
            } catch (e: UnexpectedException) {
                // Theoretically, anything outside of Denied/Ignored/Invalid/Errored
                // is an unexpected expection ( even a normal [Exception].
                // However, this is here for completeness ( to have exceptions
                // that correspond to the various [Status] groups), and to cover the
                // case when someone wants to explicitly use an UnhandledException
                // or Status group/code
                Failure(e, Result.build(e.message, null, StatusCodes.UNEXPECTED))
            } catch (e: Exception) {
                when(e) {
                    is StatusException -> Failure(e, Result.build(e.msg, e.status, StatusCodes.UNEXPECTED))
                    else -> Failure(e, Result.build(e.message, null, StatusCodes.UNEXPECTED))
                }
            }
}
