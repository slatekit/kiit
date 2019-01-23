/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.results


/**
 * Container for a Success/Failure value of type T with additional values to represent
 * a string message, code, tag, error and more.
 *
 * @tparam T      : Type T
 */
@Suppress("UNCHECKED_CAST")
sealed class Result<out T, out E> {
    abstract val success: Boolean
    abstract val code: Int
    abstract val msg: String

    companion object {

        @JvmStatic
        inline fun <T> of(f: () -> T): Result<T, String> =
                try {
                    Success(f())
                } catch (e: Exception) {
                    val err = e.message ?: ""
                    Failure(err, Codes.FAILURE.code, err)
                }

        @JvmStatic
        inline fun <T> attempt(f: () -> T): Result<T, Exception> =
                try {
                    val data = f()

                    // Avoid nested Result<T, E>
                    val result = when (data) {
                        is Result<*, *> -> (data as Result<T, Any>).toResultEx()
                        else -> Success(data)
                    }
                    result
                } catch (e: Exception) {
                    val err = e.message ?: ""
                    Failure(e, Codes.UNEXPECTED_ERROR.code, err)
                }
    }
}

/**
 * Success branch of the Result
 */
data class Success<out T>(
        val data: T,
        override val code: Int = Codes.SUCCESS.code,
        override val msg: String = "success"
) : Result<T, Nothing>() {

    override val success = true
}

/**
 * Failure branch of the result
 */
data class Failure<out E>(
        val err: E,
        override val code: Int = Codes.FAILURE.code,
        override val msg: String = "failure"
) : Result<Nothing, E>() {

    override val success = false
}
