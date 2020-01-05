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
import slatekit.results.Failure
import slatekit.results.Result
import slatekit.results.Status
import slatekit.results.Success

/**
 * Builder interface with builder functions to create the most common Successes/Failures with
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
    fun <T> success(): Result<T?, E> = Success(null, Codes.SUCCESS)
    fun <T> success(value: T): Result<T, E> = Success(value, Codes.SUCCESS)
    fun <T> success(value: T, msg: String): Result<T, E> = Success(value, msg)
    fun <T> success(value: T, code: Int): Result<T, E> = Success(value, code)
    fun <T> success(value: T, status: Status.Succeeded): Result<T, E> = Success(value, status)

    fun <T> pending(): Result<T?, E> = Success(null, Codes.PENDING)
    fun <T> pending(value: T): Result<T, E> = Success(value, Codes.PENDING)
    fun <T> pending(value: T, msg: String): Result<T, E> = Success(value, Result.status(msg, null, Codes.PENDING))
    fun <T> pending(value: T, code: Int): Result<T, E> = Success(value, Result.status(null, code, Codes.PENDING))
    fun <T> pending(value: T, status: Status.Pending): Result<T, E> = Success(value, status)

    fun <T> denied(): Result<T, E> = Failure(errorFromStr(null, Codes.DENIED), Codes.DENIED)
    fun <T> denied(msg: String): Result<T, E> = Failure(errorFromStr(msg, Codes.DENIED), Codes.DENIED)
    fun <T> denied(ex: Exception): Result<T, E> = Failure(errorFromEx(ex, Codes.DENIED), Codes.DENIED)
    fun <T> denied(err: Err): Result<T, E> = Failure(errorFromErr(err, Codes.DENIED), Codes.DENIED)

    fun <T> ignored(): Result<T, E> = Failure(errorFromStr(null, Codes.IGNORED), Codes.IGNORED)
    fun <T> ignored(msg: String): Result<T, E> = Failure(errorFromStr(msg, Codes.IGNORED), Codes.IGNORED)
    fun <T> ignored(ex: Exception): Result<T, E> = Failure(errorFromEx(ex, Codes.IGNORED), Codes.IGNORED)
    fun <T> ignored(err: Err): Result<T, E> = Failure(errorFromErr(err, Codes.IGNORED), Codes.IGNORED)

    fun <T> invalid(): Result<T, E> = Failure(errorFromStr(null, Codes.INVALID), Codes.INVALID)
    fun <T> invalid(msg: String): Result<T, E> = Failure(errorFromStr(msg, Codes.INVALID), Codes.INVALID)
    fun <T> invalid(ex: Exception): Result<T, E> = Failure(errorFromEx(ex, Codes.INVALID), Codes.INVALID)
    fun <T> invalid(err: Err): Result<T, E> = Failure(errorFromErr(err, Codes.INVALID), Codes.INVALID)

    fun <T> conflict(): Result<T, E> = Failure(errorFromStr(null, Codes.CONFLICT), Codes.CONFLICT)
    fun <T> conflict(msg: String): Result<T, E> = Failure(errorFromStr(msg, Codes.CONFLICT), Codes.CONFLICT)
    fun <T> conflict(ex: Exception): Result<T, E> = Failure(errorFromEx(ex, Codes.CONFLICT), Codes.CONFLICT)
    fun <T> conflict(err: Err): Result<T, E> = Failure(errorFromErr(err, Codes.CONFLICT), Codes.CONFLICT)

    // General purpose error, but allow user to supply the status code optionally
    // Named errored to avoid collision with Kotlin [Result.failed]
    fun <T> errored(): Result<T, E> = Failure(errorFromStr(null, get(null, Codes.ERRORED)), Codes.ERRORED)
    fun <T> errored(msg: String, status: Status.Errored? = null): Result<T, E> = Failure(errorFromStr(msg, get(status, Codes.ERRORED)), status ?: Codes.ERRORED)
    fun <T> errored(ex: Exception, status: Status.Errored? = null): Result<T, E> = Failure(errorFromEx(ex, get(status, Codes.ERRORED)), status ?: Codes.ERRORED)
    fun <T> errored(err: Err, status: Status.Errored? = null): Result<T, E> = Failure(errorFromErr(err, get(status, Codes.ERRORED)), status ?: Codes.ERRORED)
    fun <T> errored(status: Status.Errored): Result<T, E> = Failure(errorFromStr(null, get(status, Codes.ERRORED)), status)

    fun <T> unexpected(): Result<T, E> = Failure(errorFromStr(null, Codes.UNEXPECTED), Codes.UNEXPECTED)
    fun <T> unexpected(msg: String): Result<T, E> = Failure(errorFromStr(msg, Codes.UNEXPECTED), Codes.UNEXPECTED)
    fun <T> unexpected(ex: Exception): Result<T, E> = Failure(errorFromEx(ex, Codes.UNEXPECTED), Codes.UNEXPECTED)
    fun <T> unexpected(err: Err): Result<T, E> = Failure(errorFromErr(err, Codes.UNEXPECTED), Codes.UNEXPECTED)

    /**
     * Builds a status from a message.
     * NOTE: This is a minor optimization where there is no need to create a status from the
     * message if the message is null / empty.
     */
    fun msgToStatus(msg: String?, defaultStatus: Status): Status = when (msg) {
        null, "" -> defaultStatus
        else -> defaultStatus.copyMsg(msg)
    }

    /**
     * Builds a status from a message.
     * NOTE: This is a minor optimization where there is no need to create a status from the
     * message if the message is null / empty.
     */
    fun get(status: Status?, defaultStatus: Status): Status = status ?: defaultStatus
}
