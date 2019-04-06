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
import slatekit.results.Status
import slatekit.results.StatusCodes
import slatekit.results.StatusGroup

/**
 * Builder interface with builder functions to create the most common Successes/Failures with
 * customizable error type [E] with support for [String], [Exception], [Err] as error type [E]
 */
interface Builder<out E> {

    /**
     * Build the error type [E] from a [Exception]
     */
    fun errorFromEx(ex: Exception, defaultStatus: StatusGroup): E

    /**
     * Build the error type [E] from a [String]
     */
    fun errorFromStr(msg: String?, defaultStatus: StatusGroup): E

    /**
     * Build the error type [E] from a [Err]
     */
    fun errorFromErr(err: Err, defaultStatus: StatusGroup): E

    // The success(...) methods below could be 100% replaced with direct usage of top level class Success
    // But its here for completeness to be able to build all the various types
    // of successes / failures using builder methods.
    fun <T> success(): Result<T?, E> = Success(null, StatusCodes.SUCCESS)
    fun <T> success(value: T): Result<T, E> = Success(value, StatusCodes.SUCCESS)
    fun <T> success(value: T, msg: String): Result<T, E> = Success(value, msg)
    fun <T> success(value: T, code: Int): Result<T, E> = Success(value, code)
    fun <T> success(value: T, status: Status): Result<T, E> = Success(value, status)

    fun <T> pending(): Result<T?, E> = Success(null, StatusCodes.PENDING)
    fun <T> pending(value: T): Result<T, E> = Success(value, StatusCodes.PENDING)
    fun <T> pending(value: T, msg: String): Result<T, E> = Success(value, Result.status(msg, null, StatusCodes.PENDING))
    fun <T> pending(value: T, code: Int): Result<T, E> = Success(value, Result.status(null, code, StatusCodes.PENDING))
    fun <T> pending(value: T, status:Status): Result<T, E> = Success(value, status)

    fun <T> ignored(): Result<T, E> = Failure(errorFromStr(null, StatusCodes.IGNORED), StatusCodes.IGNORED)
    fun <T> ignored(msg: String): Result<T, E> = Failure(errorFromStr(msg, StatusCodes.IGNORED), StatusCodes.IGNORED)
    fun <T> ignored(ex: Exception): Result<T, E> = Failure(errorFromEx(ex, StatusCodes.IGNORED), StatusCodes.IGNORED)
    fun <T> ignored(err: Err): Result<T, E> = Failure(errorFromErr(err, StatusCodes.IGNORED), StatusCodes.IGNORED)

    fun <T> invalid(): Result<T, E> = Failure(errorFromStr(null, StatusCodes.INVALID), StatusCodes.INVALID)
    fun <T> invalid(msg: String): Result<T, E> = Failure(errorFromStr(msg, StatusCodes.INVALID), StatusCodes.INVALID)
    fun <T> invalid(ex: Exception): Result<T, E> = Failure(errorFromEx(ex, StatusCodes.INVALID), StatusCodes.INVALID)
    fun <T> invalid(err: Err): Result<T, E> = Failure(errorFromErr(err, StatusCodes.INVALID), StatusCodes.INVALID)

    fun <T> denied(): Result<T, E> = Failure(errorFromStr(null, StatusCodes.DENIED), StatusCodes.DENIED)
    fun <T> denied(msg: String): Result<T, E> = Failure(errorFromStr(msg, StatusCodes.DENIED), StatusCodes.DENIED)
    fun <T> denied(ex: Exception): Result<T, E> = Failure(errorFromEx(ex, StatusCodes.DENIED), StatusCodes.DENIED)
    fun <T> denied(err: Err): Result<T, E> = Failure(errorFromErr(err, StatusCodes.DENIED), StatusCodes.DENIED)

    fun <T> conflict(): Result<T, E> = Failure(errorFromStr(null, StatusCodes.CONFLICT), StatusCodes.CONFLICT)
    fun <T> conflict(msg: String): Result<T, E> = Failure(errorFromStr(msg, StatusCodes.CONFLICT), StatusCodes.CONFLICT)
    fun <T> conflict(ex: Exception): Result<T, E> = Failure(errorFromEx(ex, StatusCodes.CONFLICT), StatusCodes.CONFLICT)
    fun <T> conflict(err: Err): Result<T, E> = Failure(errorFromErr(err, StatusCodes.CONFLICT), StatusCodes.CONFLICT)

    fun <T> unexpected(): Result<T, E> = Failure(errorFromStr(null, StatusCodes.UNEXPECTED), StatusCodes.UNEXPECTED)
    fun <T> unexpected(msg: String): Result<T, E> = Failure(errorFromStr(msg, StatusCodes.UNEXPECTED), StatusCodes.UNEXPECTED)
    fun <T> unexpected(ex: Exception): Result<T, E> = Failure(errorFromEx(ex, StatusCodes.UNEXPECTED), StatusCodes.UNEXPECTED)
    fun <T> unexpected(err: Err): Result<T, E> = Failure(errorFromErr(err, StatusCodes.UNEXPECTED), StatusCodes.UNEXPECTED)

    // General purpose error, but allow user to supply the status code optionally
    // Named errored to avoid collision with Kotlin [Result.failed]
    fun <T> errored(): Result<T, E> = Failure(errorFromStr(null, get(null, StatusCodes.ERRORED)), StatusCodes.ERRORED)
    fun <T> errored(msg: String, status:Status? = null): Result<T, E> = Failure(errorFromStr(msg, get(status, StatusCodes.ERRORED)), status ?: StatusCodes.ERRORED)
    fun <T> errored(ex: Exception, status:Status? = null): Result<T, E> = Failure(errorFromEx(ex, get(status, StatusCodes.ERRORED)), status ?: StatusCodes.ERRORED)
    fun <T> errored(err: Err, status:Status? = null): Result<T, E> = Failure(errorFromErr(err, get(status, StatusCodes.ERRORED)), status ?: StatusCodes.ERRORED)
    fun <T> errored(status:Status): Result<T, E> = Failure(errorFromStr(null, get(status, StatusCodes.ERRORED)), status )


    /**
     * Builds a status from a message.
     * NOTE: This is a minor optimization where there is no need to create a status from the
     * message if the message is null / empty.
     */
    fun msgToStatus(msg: String?, defaultStatus: StatusGroup): Status = when (msg) {
        null, "" -> defaultStatus
        else -> defaultStatus.copyMsg(msg)
    }

    /**
     * Builds a status from a message.
     * NOTE: This is a minor optimization where there is no need to create a status from the
     * message if the message is null / empty.
     */
    fun get(status:Status?, defaultStatus: StatusGroup): StatusGroup = when (status) {
        null -> defaultStatus
        is StatusGroup -> status
        else -> defaultStatus
    }
}
