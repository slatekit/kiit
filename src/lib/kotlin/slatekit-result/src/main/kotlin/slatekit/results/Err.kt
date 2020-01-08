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

/**
 * Err is an error representation for [Outcome] and can be created from
 * 1. simple strings
 * 2. exceptions
 * 3. field with name/value
 * 4. list of strings or Errs
 */
sealed class Err {

    abstract val msg: String
    abstract val err: Throwable?
    abstract val ref: Any?

    /**
     * Different implementations for Error
     */

    /**
     * Default Error implementation to represent an error with message and optional throwable
     */
    data class ErrorInfo(override val msg: String, override val err: Throwable? = null, override val ref: Any? = null) : Err()

    /**
     * Error implementation to represent an error on a specific field
     * @param field: Name of the field causing the error e.g. "email"
     * @param value: Value of the field causing the error e.g. "some_invalid_value"
     */
    data class ErrorField(val field: String, val value: String, override val msg: String, override val err: Throwable? = null, override val ref: Any? = null) : Err()

    /**
     * Error implementation to store list of errors
     * @param errors: List of all the errors
     */
    data class ErrorList(val errors: List<Err>, override val msg: String, override val err: Throwable? = null, override val ref: Any? = null) : Err()

    /**
     * Provides easy ways to build the Err type from various sources such as strings, exceptions, field errors
     */
    companion object {

        @JvmStatic
        fun of(msg: String, ex: Throwable? = null): Err {
            return ErrorInfo(msg, ex)
        }

        @JvmStatic
        fun of(ex: Throwable): Err {
            return ErrorInfo(ex.message ?: "", ex)
        }

        @JvmStatic
        fun on(field: String, value: String, msg: String, ex: Throwable? = null): Err {
            return ErrorField(field, value, msg, ex)
        }

        @JvmStatic
        fun ex(ex: Exception): Err {
            return ErrorInfo(ex.message ?: "", ex)
        }

        @JvmStatic
        fun obj(err: Any): Err {
            return ErrorInfo(err.toString(), null, err)
        }

        @JvmStatic
        fun code(status: Status): Err {
            return ErrorInfo(status.msg)
        }

        @JvmStatic
        fun list(errors: List<String>, msg: String?): ErrorList {
            return ErrorList(errors.map { ErrorInfo(it) }, msg ?: "Error occurred")
        }

        @JvmStatic
        fun build(error: Any?): Err {
            return when (error) {
                null -> Err.of(Codes.UNEXPECTED.msg)
                is Err -> error
                is String -> Err.of(error)
                is Exception -> Err.of(error)
                else -> Err.obj(error)
            }
        }
    }
}

/**
 * Error implementation extending from exception
 */
data class ExceptionErr(val msg: String, val err: Err) : Exception(msg)
