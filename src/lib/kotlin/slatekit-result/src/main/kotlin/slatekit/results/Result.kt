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

package slatekit.results


/**
 * Container for a Success/Failure value of type T with additional values to represent
 * a string message, code, error and more.
 *
 * @tparam T      : Type T
 */
sealed class Result<out T, out E> {

    /**
     * Optional status code is defaulted in the [Success] and [Failure]
     * branches using the predefined set of codes in [StatusCodes]
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
     * Success(42).withStatus( StatusCodes. ) // Result<String,E>
     * ```
     */
    @Suppress("NOTHING_TO_INLINE")
    inline fun withStatus(successCode: Status, failureCode: Status): Result<T, E> =
        when (this) {
            is Success -> this.copy(status = successCode)
            is Failure -> this.copy(status = failureCode)
        }


    /**
     *
     * Applies the supplied message to this result
     *
     * @param successCode: The [Status] code to apply if success
     * @param failureCode: The [Status] code to apply if failure
     *
     * # Example
     * ```
     * Success(42).withStatus( StatusCodes. ) // Result<String,E>
     * ```
     */
    @Suppress("NOTHING_TO_INLINE")
    inline fun withMessage(successMessage: String, failureMessage: String): Result<T, E> =
            when (this) {
                is Success -> this.copy(status = status.copyMsg(successMessage))
                is Failure -> this.copy(status = status.copyMsg(failureMessage))
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
            val newError = Result.error(error)
            if (retainStatus) {
                Failure(newError, status)
            } else {
                when (newError) {
                    is Status -> Failure(newError, newError)
                    else -> Failure(newError, status)
                }
            }
            this as Failure<Err>
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
                is Err -> Failure(Exception(this.error.toString()), this.status)
                null -> Failure(Exception(this.status.msg), this.status)
                else -> Failure(Exception(this.error.toString()), this.status)
            }
        }
    }


    companion object {

        /**
         * Build a Outcome<T> ( type alias ) for Result<T,Err> using the supplied function
         */
        @JvmStatic
        inline fun <T> of(f: () -> T): Outcome<T> = build(f, { ex -> Err.of(ex) })


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
                    Failure(e, build(e.msg, e.status, StatusCodes.DENIED))
                } catch (e: IgnoredException) {
                    Failure(e, build(e.msg, e.status, StatusCodes.IGNORED))
                } catch (e: InvalidException) {
                    Failure(e, build(e.msg, e.status, StatusCodes.INVALID))
                } catch (e: ErroredException) {
                    Failure(e, build(e.msg, e.status, StatusCodes.ERRORED))
                } catch (e: UnexpectedException) {
                    // Theoretically, anything outside of Denied/Ignored/Invalid/Errored
                    // is an unexpected expection ( even a normal [Exception].
                    // However, this is here for completeness ( to have exceptions
                    // that correspond to the various [Status] groups), and to cover the
                    // case when someone wants to explicitly use an UnhandledException
                    // or Status group/code
                    Failure(e, build(e.message, null, StatusCodes.UNEXPECTED))
                } catch (e: Exception) {
                    Failure(e, build(e.message, null, StatusCodes.UNEXPECTED))
                }

        /**
         * Build a Notice<T> ( type alias ) for Result<T,String> using the supplied function
         */
        @JvmStatic
        inline fun <T> notice(f: () -> T): Notice<T> = build(f, { e -> e.message ?: StatusCodes.ERRORED.msg })


        /**
         * Build a Result<T,E> using the supplied callback and error handler
         */
        @JvmStatic
        inline fun <T, E> build(f: () -> T, onError: (Exception) -> E): Result<T, E> =
            try {
                val data = f()
                Success(data)
            } catch (e: Exception) {
                Failure(onError(e))
            }


        @JvmStatic
        fun error(error: Any?): Err {
            return when (error) {
                null -> Err.of(StatusCodes.UNEXPECTED.msg)
                is Err -> error
                is String -> Err.of(error)
                is Exception -> Err.of(error)
                else -> ErrorWithObject(error.toString(), error)
            }
        }


        @JvmStatic
        fun status(msg: String?, code: Int?, status: Status): Status {
            // NOTE: There is small optimization here to avoid creating a new instance
            // of [Status] if the msg/code are empty and or they are the same as Success.
            if (code == null && msg == null || msg == "") return status
            if (code == status.code && msg == null) return status
            if (code == status.code && msg == status.msg) return status
            return status.copyAll(msg ?: status.msg, code ?: status.code)
        }


        @JvmStatic
        fun build(msg: String?, rawStatus:Status?, status: Status): Status {
            // NOTE: There is small optimization here to avoid creating a new instance
            // of [Status] if the msg/code are empty and or they are the same as Success.
            if(msg == null && rawStatus == null) return status
            if(msg == null && rawStatus != null) return rawStatus
            if(msg != null && rawStatus == null) return status.copyMsg(msg)
            if(msg != null && rawStatus != null) return rawStatus.copyMsg(msg)
            return status
        }
    }
}


/**
 * Success branch of the Result
 *
 * @param value  : Value representing the success
 * @param status : Optional status code as [Status]
 */
data class Success<out T>(
    val value: T,
    override val status: Status = StatusCodes.SUCCESS
) : Result<T, Nothing>() {

    // NOTE: These overloads are here for convenience + Java Interoperability
    /**
     * Initialize using explicitly supplied message
     * @param value : Value representing the success
     * @param msg   : Optional message for the status
     *
     * NOTE: There is small optimization here to avoid creating a new instance
     * of [Status] if the msg/code are empty and or they are the same as [StatusCodes.SUCCESS].
     */
    constructor(value: T, msg: String)
            : this(value, Result.status(msg, null, StatusCodes.SUCCESS))

    /**
     * Initialize using explicitly supplied code
     * @param value : Value representing the success
     * @param code  : Optional code for the status
     *
     * NOTE: There is small optimization here to avoid creating a new instance
     * of [Status] if the msg/code are empty and or they are the same as [StatusCodes.SUCCESS].
     */
    constructor(value: T, code: Int)
            : this(value, Result.status(null, code, StatusCodes.SUCCESS))

    /**
     * Initialize using explicitly supplied message and code
     * @param value : Value representing the success
     * @param msg   : Optional message for the status
     * @param code  : Optional code for the status
     *
     * NOTE: There is small optimization here to avoid creating a new instance
     * of [Status] if the msg/code are empty and or they are the same as [StatusCodes.SUCCESS].
     */
    constructor(value: T, msg: String? = null, code: Int? = null)
            : this(value, Result.status(msg, code, StatusCodes.SUCCESS))
}


/**
 * Failure branch of the result
 *
 * @param error  : Error representing the failure
 * @param status : Optional status code as [Status]
 */
data class Failure<out E>(
    val error: E,
    override val status: Status = StatusCodes.ERRORED
) : Result<Nothing, E>() {

    // NOTE: These overloads are here for convenience + Java Interoperability
    /**
     * Initialize using explicitly supplied message
     * @param error : Error representing the failure
     * @param msg   : Optional message for the status
     *
     * NOTE: There is small optimization here to avoid creating a new instance
     * of [Status] if the msg/code are empty and or they are the same as [StatusCodes.ERRORED].
     */
    constructor(error: E, msg: String)
            : this(error, Result.status(msg, null, StatusCodes.ERRORED))

    /**
     * Initialize using explicitly supplied code
     * @param error : Error representing the failure
     * @param code  : Optional code for the status
     *
     * NOTE: There is small optimization here to avoid creating a new instance
     * of [Status] if the msg/code are empty and or they are the same as [StatusCodes.ERRORED].
     */
    constructor(error: E, code: Int)
            : this(error, Result.status(null, code, StatusCodes.ERRORED))

    /**
     * Initialize using explicitly supplied message and code
     * @param error : Error representing the failure
     * @param msg   : Optional message for the status
     * @param code  : Optional code for the status
     *
     * NOTE: There is small optimization here to avoid creating a new instance
     * of [Status] if the msg/code are empty and or they are the same as [StatusCodes.ERRORED].
     */
    constructor(error: E, msg: String? = null, code: Int? = null)
            : this(error, Result.status(msg, code, StatusCodes.ERRORED))
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
inline fun <T,E> Result<Result<T, E>,E>.inner(): Result<T,E> = this.fold( { it }, { Failure(it) } )


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

