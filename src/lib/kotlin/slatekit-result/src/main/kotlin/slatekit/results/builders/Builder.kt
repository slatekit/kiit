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
 * Provides convenient ways to build the most common Successes/Failures with
 * customizable error type [E] with support for [String], [Exception], [Err] as error type [E]
 */
interface Builder<out E> {

    /**
     * Build the error type [E] from a [Exception]
     */
    fun errorFromEx(ex: Exception, defaultStatus: Status): E

    /**
     * Build the error type [E] from a [String]
     */
    fun errorFromStr(msg: String?, defaultStatus: Status): E

    /**
     * Build the error type [E] from a [Err]
     */
    fun errorFromErr(err: Err, defaultStatus: Status): E

    // The success(...) methods below could be 100% replaced with direct usage of top level class Success
    // But its here for completeness to be able to build all the various types
    // of successes / failures using builder methods.
    fun <T> success(): Result<T?, E> = Success(null)
    fun <T> success(value: T): Result<T, E> = Success(value)
    fun <T> success(value: T, msg: String): Result<T, E> = Success(value, msg)
    fun <T> success(value: T, code: Int): Result<T, E> = Success(value, code)
    fun <T> success(value: T, status: Passed.Succeeded): Result<T, E> = Success(value, status)

    fun <T> pending(): Result<T?, E> = Success(null, status = Codes.PENDING)
    fun <T> pending(value: T): Result<T, E> = Success(value, Codes.PENDING)
    fun <T> pending(value: T, msg: String): Result<T, E> = Success(value, Status.ofCode(msg, null, Codes.PENDING))
    fun <T> pending(value: T, code: Int): Result<T, E> = Success(value, Status.ofCode(null, code, Codes.PENDING))
    fun <T> pending(value: T, status: Passed.Pending): Result<T, E> = Success(value, status)

    fun <T> denied(): Result<T, E> = Failure(errorFromStr(null, Codes.DENIED), Codes.DENIED)
    fun <T> denied(msg: String): Result<T, E> = Failure(errorFromStr(msg, Codes.DENIED), Codes.DENIED)
    fun <T> denied(ex: Exception, status: Failed.Denied? = null): Result<T, E> = Failure(errorFromEx(ex, Codes.DENIED), status ?: Codes.DENIED)
    fun <T> denied(err: Err, status: Failed.Denied? = null): Result<T, E> = Failure(errorFromErr(err, Codes.DENIED), status ?: Codes.DENIED)
    fun <T> denied(status:Failed.Denied): Result<T, E> = Failure(errorFromStr(null, get(status, Codes.DENIED)), status)

    fun <T> ignored(): Result<T, E> = Failure(errorFromStr(null, Codes.IGNORED), Codes.IGNORED)
    fun <T> ignored(msg: String): Result<T, E> = Failure(errorFromStr(msg, Codes.IGNORED), Codes.IGNORED)
    fun <T> ignored(ex: Exception, status: Failed.Ignored? = null): Result<T, E> = Failure(errorFromEx(ex, Codes.IGNORED), status ?: Codes.IGNORED)
    fun <T> ignored(err: Err, status: Failed.Ignored? = null): Result<T, E> = Failure(errorFromErr(err, Codes.IGNORED), status ?: Codes.IGNORED)
    fun <T> ignored(status:Failed.Ignored): Result<T, E> = Failure(errorFromStr(null, get(status, Codes.IGNORED)), status)

    fun <T> invalid(): Result<T, E> = Failure(errorFromStr(null, Codes.INVALID), Codes.INVALID)
    fun <T> invalid(msg: String): Result<T, E> = Failure(errorFromStr(msg, Codes.INVALID), Codes.INVALID)
    fun <T> invalid(ex: Exception, status: Failed.Invalid? = null): Result<T, E> = Failure(errorFromEx(ex, Codes.INVALID), status ?: Codes.INVALID)
    fun <T> invalid(err: Err, status:Failed.Invalid? = null): Result<T, E> = Failure(errorFromErr(err, Codes.INVALID), status ?: Codes.INVALID)
    fun <T> invalid(status:Failed.Invalid): Result<T, E> = Failure(errorFromStr(null, get(status, Codes.INVALID)), status)

    fun <T> conflict(): Result<T, E> = Failure(errorFromStr(null, Codes.CONFLICT), Codes.CONFLICT)
    fun <T> conflict(msg: String): Result<T, E> = Failure(errorFromStr(msg, Codes.CONFLICT), Codes.CONFLICT)
    fun <T> conflict(ex: Exception, status:Failed.Errored? = null): Result<T, E> = Failure(errorFromEx(ex, Codes.CONFLICT), status ?: Codes.CONFLICT)
    fun <T> conflict(err: Err, status:Failed.Errored? = null): Result<T, E> = Failure(errorFromErr(err, Codes.CONFLICT), status ?: Codes.CONFLICT)
    fun <T> conflict(status:Failed.Errored): Result<T, E> = Failure(errorFromStr(null, Codes.CONFLICT), status)

    // General purpose error, but allow user to supply the status code optionally
    // Named errored to avoid collision with Kotlin [Result.failed]
    fun <T> errored(): Result<T, E> = Failure(errorFromStr(null, get(null, Codes.ERRORED)), Codes.ERRORED)
    fun <T> errored(msg: String, status: Failed.Errored? = null): Result<T, E> = Failure(errorFromStr(msg, get(status, Codes.ERRORED)), status ?: Codes.ERRORED)
    fun <T> errored(ex: Exception, status: Failed.Errored? = null): Result<T, E> = Failure(errorFromEx(ex, get(status, Codes.ERRORED)), status ?: Codes.ERRORED)
    fun <T> errored(err: Err, status: Failed.Errored? = null): Result<T, E> = Failure(errorFromErr(err, get(status, Codes.ERRORED)), status ?: Codes.ERRORED)
    fun <T> errored(status: Failed.Errored): Result<T, E> = Failure(errorFromStr(null, get(status, Codes.ERRORED)), status)

    fun <T> unexpected(): Result<T, E> = Failure(errorFromStr(null, Codes.UNEXPECTED), Codes.UNEXPECTED)
    fun <T> unexpected(msg: String): Result<T, E> = Failure(errorFromStr(msg, Codes.UNEXPECTED), Codes.UNEXPECTED)
    fun <T> unexpected(ex: Exception, status: Failed.Unknown? = null): Result<T, E> = Failure(errorFromEx(ex, Codes.UNEXPECTED), status ?: Codes.UNEXPECTED)
    fun <T> unexpected(err: Err, status: Failed.Unknown? = null): Result<T, E> = Failure(errorFromErr(err, Codes.UNEXPECTED), status ?: Codes.UNEXPECTED)
    fun <T> unexpected(status: Failed.Unknown): Result<T, E> = Failure(errorFromStr(null, get(status, Codes.UNEXPECTED)), status)

    /**
     * Builds a status from a message.
     * NOTE: This is a minor optimization where there is no need to create a status from the
     * message if the message is null / empty.
     */
    fun get(status: Status?, defaultStatus: Status): Status = status ?: defaultStatus

    /**
     * Build a Result<T,E> using the supplied condition and default error builders
     */
    fun <T> of(condition: Boolean, t: T?,
               err:String? = null,
               success:Passed.Succeeded = Codes.SUCCESS,
               failure:Failed.Errored = Codes.ERRORED): Result<T, E> {
        return if (!condition)
            errored(failure)
        else if (t == null)
            errored(failure)
        else
            success(t, success)
    }

    /**
     * Build a Result<T,E> for a possible null value
     */
    fun <T> of(t: T?): Result<T,E> = when (t) {
            null -> errored("null")
            else -> success(t)
    }
}
