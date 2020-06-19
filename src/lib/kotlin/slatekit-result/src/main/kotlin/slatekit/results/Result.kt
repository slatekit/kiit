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

package slatekit.results

import slatekit.results.builders.Notices
import slatekit.results.builders.Outcomes
import slatekit.results.builders.Tries

/**
 * Models successes and failures with optional status codes.
 * This is similar to the Result type from languages such as Rust, Swift, Kotlin, Try from Scala.
 *
 *
 * DESIGN
 * While similar to other implementations, there are a few major differences
 * 1. Flexible error	: Error type on the Failure branch can be anything, Exception, Err, String.
 * 2. Status Codes      : Logical groups of status codes to categorize errors, which can be converted to Http
 * 3. Sensible defaults	: Default Error types, and builders are provided to reduce custom errors / boiler-plate
 *
 *
 * NOTES:
 * 1. The success value is of type T
 * 2. The failure value if of type E
 * 3. The status code is optional and initialized with sensible defaults
 *
 */
sealed class Result<out T, out E> {

    /**
     * Optional status code is defaulted in the [Success] and [Failure]
     * branches using the predefined set of codes in [Codes]
     */
    abstract val status: Status

    /**
     * These are here for convenience both internally and externally
     */
    val success: Boolean get() = this is Success
    val code: Int get() = status.code
    val msg: String get() = status.msg

    /**
     * Applies supplied function `f` if this is a [Success]
     *
     * @param f: the function to apply
     *
     * # Example
     * ```
     * val r1 = Success("Superman").map { "Clark Kent" }  // Success("Clark Kent")
     * val r2 = Failure("Unknown" ).map { "???"        }  // Failure("Unknown")
     * ```
     */
    inline fun <T2> map(f: (T) -> T2): Result<T2, E> = when (this) {
        is Success -> Success(f(this.value), this.status)
        is Failure -> this
    }

    /**
     * Applies supplied function `f` if this is a [Failure] to transform the error type
     *
     * @param f: the function to apply
     *
     * # Example
     * ```
     * val r1 = Success("Superman").map { "Clark Kent" }  // Success("Clark Kent")
     * val r2 = Failure("Unknown" ).map { "???"        }  // Failure("Unknown")
     * ```
     */
    inline fun <E2> mapError(f: (E) -> E2): Result<T, E2> = when (this) {
        is Success -> this
        is Failure -> Failure(f(this.error), this.status)
    }

    /**
     * Applies `onSuccess` if this is a [Success] or `onError` if this a [Failure]
     *
     * @param onSuccess: The function to apply if this is a [Success]
     * @param onFailure: The function to apply if this is a [Failure]
     *
     * # Example:
     * ```
     * val result : Result<String,Err> = someOperation()
     * result.fold(
     *      { println( "operation succeeded" ) },
     *      { println( "operation failed"    ) }
     * )
     * ```
     */
    inline fun <T2> fold(onSuccess: (T) -> T2, onFailure: (E) -> T2): T2 {
        return when (this) {
            is Success -> onSuccess(this.value)
            is Failure -> onFailure(this.error)
        }
    }

    /**
     * Returns the result of supplied function `f` if this is a [Success], or false otherwise
     *
     * @param f: the function to apply
     *
     * # Example
     * ```
     * Success(42).exists { it >= 42 } // true
     * Success(40).exists { it >= 42 } // false
     * ```
     */
    inline fun exists(f: (T) -> Boolean): Boolean =
        when (this) {
            is Success -> f(this.value)
            is Failure -> false
        }

    /**
     * Returns the value from this [Success] or null if this is a [Failure]
     *
     * # Example
     * ```
     * Success(42).getOrNull  // 42
     * Failure(40).getOrNull  // null
     * ```
     */
    @Suppress("NOTHING_TO_INLINE")
    inline fun getOrNull(): T? = when (this) {
        is Success -> this.value
        is Failure -> null
    }

    /**
     *
     * Applies the supplied function if this is a [Success]
     *
     * @param f: the function to apply
     *
     * # Example
     * ```
     * Success(42).onSuccess { println(it) } // 42
     * Failure(40).onSuccess { println(it) } // function not applied
     * ```
     */
    inline fun onSuccess(f: (T) -> Unit): Result<T, E> = when (this) {
        is Success -> {
            f(this.value)
            this
        }
        is Failure -> this
    }

    /**
     *
     * Applies the supplied function if this is a [Failure]
     *
     * @param f: the function to apply
     *
     * # Example
     * ```
     * Success(42).onFailure { println(it) } // function not applied
     * Failure(40).onFailure { println(it) } // 40
     * ```
     */
    inline fun onFailure(f: (E) -> Unit): Result<T, E> = when (this) {
        is Success -> this
        is Failure -> {
            f(this.error)
            this
        }
    }

    /**
     *
     * Applies the supplied functions to transform this Result
     *
     * @param onSuccess: The function to apply for a [Success]
     * @param onFailure: The function to apply for a [Failure]
     *
     * # Example
     * ```
     * Success(42).transform( { "$it" }, { "error"} ) // Result<String,E>
     * ```
     */
    inline fun <T2, E2> transform(onSuccess: (T) -> Result<T2, E2>, onFailure: (E) -> Result<T2, E2>): Result<T2, E2> =
        when (this) {
            is Success -> onSuccess(this.value)
            is Failure -> onFailure(this.error)
        }

    /**
     *
     * Applies the supplied status to this result
     *
     * @param successCode: The [Status] code to apply if success
     * @param failureCode: The [Status] code to apply if failure
     *
     * # Example
     * ```
     * Success(42).withStatus( Codes. ) // Result<String,E>
     * ```
     */
    @Suppress("NOTHING_TO_INLINE")
    inline fun withStatus(passedStatus: Passed, failedStatus: Failed): Result<T, E> =
        when (this) {
            is Success -> this.copy(status = passedStatus)
            is Failure -> this.copy(status = failedStatus)
        }

    /**
     * Transform this to a Notice (type alias ) with error type of [String]
     *
     * # Example
     * ```
     * val err1:Result<Int,Exception> = Failure(Exception("Some error"))
     * val err2:Result<Int,String> = err1.toNotice()
     * ```
     */
    @Suppress("UNCHECKED_CAST")
    fun toNotice(): Notice<T> = when (this) {
        is Success -> this
        is Failure -> {
            when (this.error) {
                is String -> this as Notice<T>
                is Exception -> Failure(this.error.message ?: "", this.status)
                else -> Failure(this.error.toString(), this.status)
            }
        }
    }

    /**
     * Transform this to an Outcome (type alias ) with error type of [Err]
     *
     * # Example
     * ```
     * val err1:Result<Int,String> = Failure("Some error")
     * val err2:Result<Int,Err>    = err1.toOutcome()
     * ```
     */
    @Suppress("UNCHECKED_CAST")
    fun toOutcome(retainStatus: Boolean = true): Outcome<T> = when (this) {
        is Success -> this
        is Failure -> {
            val err = when (this.error) {
                null -> Err.of(Codes.UNEXPECTED.msg)
                is Err -> error
                is String -> Err.of(error)
                is Exception -> Err.ex(error)
                else -> Err.obj(error)
            }
            when (retainStatus) {
                false -> Failure(err)
                true -> Failure(err, this.status)
            }
        }
    }

    /**
     * Transform this to a Try (type alias ) with error type of [Exception]
     *
     * # Example
     * ```
     * val err1:Result<Int,String> = Failure("Some error")
     * val err2:Result<Int,Exception> = err1.toTry()
     * ```
     */
    @Suppress("UNCHECKED_CAST")
    fun toTry(): Try<T> = when (this) {
        is Success -> this
        is Failure -> {
            when (this.error) {
                is Exception -> this as Try<T>
                is Err -> Failure(ExceptionErr(this.error.toString(), this.error), this.status)
                null -> Failure(Exception(this.status.msg), this.status)
                else -> Failure(Exception(this.error.toString()), this.status)
            }
        }
    }

    companion object {

        @JvmStatic
        fun <T> attempt(f: () -> T): Try<T> = Tries.of(f)

        @JvmStatic
        fun <T> outcome(f: () -> T): Outcome<T> = Outcomes.of(f)

        @JvmStatic
        fun <T> message(f: () -> T): Notice<T> = Notices.of(f)
    }
}

/**
 * Success branch of the Result
 *
 * @param value : Value representing the success
 * @param status : Optional status code as [Status]
 */
data class Success<out T>(
    val value: T,
    override val status: Passed
    ) : Result<T, Nothing>() {

    // NOTE: These overloads are here for convenience + Java Interoperability
    /**
     * Initialize using explicitly supplied message
     * @param value : Value representing the success
     *
     * NOTE: There is small optimization here to avoid creating a new instance
     * of [Status] if the msg/code are empty and or they are the same as [Codes.SUCCESS].
     */
    constructor(value: T) : this(value, Codes.SUCCESS)

    /**
     * Initialize using explicitly supplied message
     * @param value : Value representing the success
     * @param msg : Optional message for the status
     *
     * NOTE: There is small optimization here to avoid creating a new instance
     * of [Status] if the msg/code are empty and or they are the same as [Codes.SUCCESS].
     */
    constructor(value: T, msg: String) : this(value, Status.ofCode<Passed>(msg, null, Codes.SUCCESS))

    /**
     * Initialize using explicitly supplied code
     * @param value : Value representing the success
     * @param code : Optional code for the status
     *
     * NOTE: There is small optimization here to avoid creating a new instance
     * of [Status] if the msg/code are empty and or they are the same as [Codes.SUCCESS].
     */
    constructor(value: T, code: Int) : this(value, Status.ofCode<Passed>(null, code, Codes.SUCCESS))

    /**
     * Initialize using explicitly supplied message and code
     * @param value : Value representing the success
     * @param msg : Optional message for the status
     * @param code : Optional code for the status
     *
     * NOTE: There is small optimization here to avoid creating a new instance
     * of [Status] if the msg/code are empty and or they are the same as [Codes.SUCCESS].
     */
    constructor(value: T, msg: String?, code: Int?) : this(value, Status.ofCode<Passed>(msg, code, Codes.SUCCESS))


    companion object {
        fun <T> pending(value:T, status:Passed.Pending? = null):Success<T> = Success(value, status ?: Codes.PENDING)
    }

}

/**
 * Failure branch of the result
 *
 * @param error : Error representing the failure
 * @param status : Optional status code as [Status]
 */
data class Failure<out E> (
    val error: E,
    override val status: Failed
) : Result<Nothing, E>() {

    // NOTE: These overloads are here for convenience + Java Interoperability
    /**
     * Initialize using explicitly supplied message
     * @param error : Error representing the failure
     *
     * NOTE: There is small optimization here to avoid creating a new instance
     * of [Status] if the msg/code are empty and or they are the same as [Codes.ERRORED].
     */
    constructor(error: E) : this(error, Codes.ERRORED)

    /**
     * Initialize using explicitly supplied message
     * @param error : Error representing the failure
     * @param msg : Optional message for the status
     *
     * NOTE: There is small optimization here to avoid creating a new instance
     * of [Status] if the msg/code are empty and or they are the same as [Codes.ERRORED].
     */
    constructor(error: E, msg: String) : this(error, Status.ofCode<Failed>(msg, null, Codes.ERRORED))

    /**
     * Initialize using explicitly supplied code
     * @param error : Error representing the failure
     * @param code : Optional code for the status
     *
     * NOTE: There is small optimization here to avoid creating a new instance
     * of [Status] if the msg/code are empty and or they are the same as [Codes.ERRORED].
     */
    constructor(error: E, code: Int) : this(error, Status.ofCode<Failed>(null, code, Codes.ERRORED))

    /**
     * Initialize using explicitly supplied message and code
     * @param error : Error representing the failure
     * @param msg : Optional message for the status
     * @param code : Optional code for the status
     *
     * NOTE: There is small optimization here to avoid creating a new instance
     * of [Status] if the msg/code are empty and or they are the same as [Codes.ERRORED].
     */
    constructor(error: E, msg: String?, code: Int?) : this(error, Status.ofCode<Failed>(msg, code, Codes.ERRORED))

    companion object {
        fun <E> denied    (err: E, status:Failed.Denied     ? = null):Failure<E> = Failure(err, status ?: Codes.DENIED)
        fun <E> ignored   (err: E, status:Failed.Ignored    ? = null):Failure<E> = Failure(err, status ?: Codes.IGNORED)
        fun <E> invalid   (err: E, status:Failed.Invalid    ? = null):Failure<E> = Failure(err, status ?: Codes.INVALID)
        fun <E> errored   (err: E, status:Failed.Errored    ? = null):Failure<E> = Failure(err, status ?: Codes.ERRORED)
        fun <E> unexpected(err: E, status:Failed.Unexpected ? = null):Failure<E> = Failure(err, status ?: Codes.UNEXPECTED)
    }

}

/**
 * Applies supplied function `f` if this is a [Success]
 *
 * @param f: the function to apply
 *
 * # Example
 * ```
 * val r1 = Success("Superman").flatMap { Success("Clark Kent") }  // Success("Clark Kent")
 * val r2 = Failure("Unknown" ).flatMap { Success("???")        }  // Failure("Unknown")
 * ```
 */
inline fun <T1, T2, E> Result<T1, E>.flatMap(f: (T1) -> Result<T2, E>): Result<T2, E> = this.then(f)

/**
 * Applies supplied function `f` if this is a [Success]
 *
 * @param f: the function to apply
 *
 * # Example
 * ```
 * val r1 = Success("Superman").then { Success("Clark Kent") }  // Success("Clark Kent")
 * val r2 = Failure("Unknown" ).then { Success("???")        }  // Failure("Unknown")
 * ```
 */
inline fun <T1, T2, E> Result<T1, E>.then(f: (T1) -> Result<T2, E>): Result<T2, E> =
    when (this) {
        is Success -> f(this.value)
        is Failure -> this
    }

/**
 * Applies supplied function `op` if this is a [Success]. The difference to flatMap / then is that the whole
 * Result is provided as an input
 *
 * @param op: The function to apply if this is a [Success]
 *
 * # Example:
 * ```
 * val result : Result<String,Err> = someOperation()
 * result.fold(
 *      { println( "operation succeeded" ) },
 *      { println( "operation failed"    ) }
 * )
 * ```
 */
inline fun <T, E> Result<T, E>.orElse(other: (Result<T, E>)): Result<T, E> {
    return when (this) {
        is Success -> this
        is Failure -> other
    }
}


/**
 * Applies supplied function `op` if this is a [Success]. The difference to flatMap / then is that the whole
 * Result is provided as an input
 *
 * @param op: The function to apply if this is a [Success]
 *
 * # Example:
 * ```
 * val result : Result<String,Err> = someOperation()
 * result.fold(
 *      { println( "operation succeeded" ) },
 *      { println( "operation failed"    ) }
 * )
 * ```
 */
inline fun <T1, T2, E> Result<T1, E>.operate(op: (Result<T1, E>) -> Result<T2, E>): Result<T2, E> {
    return when (this) {
        is Success -> op(this)
        is Failure -> this
    }
}

/**
 * Applies supplied function `f` if this is a [Failure] to transform the error type
 *
 * @param f: the function to apply
 *
 * # Example
 * ```
 * val r1 = Success("Superman").flatMapError { "Clark Kent" }  // Success("Clark Kent")
 * val r2 = Failure("Unknown" ).flatMapError { "???"        }  // Failure("???")
 * ```
 */
inline fun <T, E, E2> Result<T, E>.flatMapError(f: (E) -> Result<T, E2>): Result<T, E2> = when (this) {
    is Success -> this
    is Failure -> f(this.error)
}

/**
 * Returns the value from this [Success] or the default value supplied if [Failure]
 *
 * # Example
 * ```
 * Success("Superman").getOrElse("???")  // "Superman"
 * Failure("Unknown" ).getOrElse("???")  // "???"
 * ```
 */
inline fun <T, E> Result<T, E>.getOrElse(f: () -> T): T =
    when (this) {
        is Success -> this.value
        is Failure -> f()
    }

/**
 * Gets the inner value in a nested Result
 *
 * # Example
 * ```
 * val r1 = Success(Success("Superman")).inner() // Success("Clark Kent")
 * ```
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T, E> Result<Result<T, E>, E>.inner(): Result<T, E> = this.fold({ it }, { Failure(it) })

/**
 * Returns true if this is a [Success] with the value supplied, or false otherwise
 *
 * # Example
 * ```
 * Success(42).contains(42) // true
 * Success(40).contains(42) // false
 * Failure(39).contains(42) // false
 * ```
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T, E> Result<T, E>.contains(i: T): Boolean =
    when (this) {
        is Success -> i == this.value
        is Failure -> false
    }

/**
 * Builds a Result as a [Success] with the value supplied
 *
 * # Example
 * ```
 * 42.success() // Success(42)
 * ```
 */
fun <T> T.toSuccess(): Result<T, Nothing> = Success(this)

/**
 * Builds a Result as a [Failure] with the value supplied
 *
 * # Example
 * ```
 * 400.failure() // Failure(400)
 * ```
 */
fun <E> E.toFailure(): Result<Nothing, E> = Failure(this)
