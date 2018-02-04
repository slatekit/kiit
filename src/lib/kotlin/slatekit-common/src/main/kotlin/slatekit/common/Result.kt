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

package slatekit.common

import slatekit.common.results.FAILURE
import slatekit.common.results.ResultChecks
import slatekit.common.results.ResultFuncs

/**
<slate_header>
author: Kishore Reddy
url: https://github.com/kishorereddy/scala-slate
copyright: 2015 Kishore Reddy
license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
desc: a scala micro-framework
usage: Please refer to license on github for more info.
</slate_header>
 */


/**
 * Container for a Success/Failure value of type T with additional values to represent
 * a string message, code, tag, error and more.
 *
 * @tparam T      : Type T
 */
@Suppress("UNCHECKED_CAST")
interface Result<out T> : ResultChecks {
    val success: Boolean
    val code: Int
    val value: T?
    val msg: String?
    val err: Exception?
    val tag: String?
    val ref: Any?

    override fun statusCode() = code


    fun <M> map(f: (T) -> M): Result<M> = value?.let { v -> ResultFuncs.success(f(v), msg, tag, ref) } ?: this as Result<M>


    fun <M> flatMap(f: (T) -> Result<M>): Result<M> = value?.let { v -> f(v) } ?: this as Result<M>


    companion object Results {

        val none = Failure<Boolean>(FAILURE, null, null, null, null)


        fun <T> attempt(call: () -> T): Result<T> {
            return try {
                val data = call()
                if (data is Result<*>) {
                    data as Result<T>
                }
                else {
                    ResultFuncs.success(data)
                }
            }
            catch (e: Exception) {
                ResultFuncs.unexpectedError<T>(e.message, e)
            }
        }



        fun <T> tryLog(name:String, desc:String, rethrow:Boolean, call: () -> T): Result<T> {
            return try {
                val data = call()
                if (data is Result<*>) {
                    data as Result<T>
                }
                else {
                    ResultFuncs.success(data)
                }
            }
            catch (e: Exception) {
                println("Error during: $name")
                println("$desc")
                println(e.message)
                println(e)

                if(rethrow) {
                    throw e
                }
                ResultFuncs.unexpectedError<T>(e.message, e)
            }
        }
    }
}


/**
 * Success branch of the Result
 */
data class Success<out T>(
        override val code: Int,
        private val data: T,
        override val msg: String?,
        override val tag: String?,
        override val ref: Any?
) : Result<T>, ResultChecks {

    override val success: Boolean get() = true


    override val value: T? get() = data


    override val err: Exception? get() = null
}


/**
 * Failure branch of the result
 */
data class Failure<out T>(
        override val code: Int,
        override val err: Exception?,
        override val msg: String?,
        override val tag: String?,
        override val ref: Any?
) : Result<T>, ResultChecks {

    override val success: Boolean get() = false


    override val value: T? get() = null
}


fun <T> Result<T>.getOrElse(default: () -> T): T = value ?: default()


