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

import slatekit.common.results.*

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
sealed class Result<out E, out T> {
    abstract val success: Boolean
    abstract val code: Int
    abstract val msg: String


    companion object {


        inline fun <T> of(f: () -> T): Result<String, T> =
            try {
                Success(f())
            } catch (e: Exception) {
                val err = e.message ?: ""
                Failure(err, FAILURE,err)
            }


        inline fun <T> attempt(f: () -> T): Result<Exception, T> =
            try {
                Success(f())
            } catch (e: Exception) {
                val err = e.message ?: ""
                Failure(e, UNEXPECTED_ERROR,err)
            }

    }
}


/**
 * Success branch of the Result
 */
data class Success<out T>(
        val data: T,
        override val code: Int = SUCCESS,
        override val msg: String = ""
) : Result<Nothing, T>() {

    override val success = true
}


/**
 * Failure branch of the result
 */
data class Failure<out E>(
        val err: E,
        override val code: Int = FAILURE,
        override val msg: String = ""
) : Result<E, Nothing>() {

    override val success = false
}


typealias ResultMsg<T> = Result<String   , T>
typealias ResultEx<T>  = Result<Exception, T>


inline fun <E, T1, T2> Result<E, T1>.map(f: (T1) -> T2): Result<E, T2> =
    when (this) {
        is Success -> Success(f(this.data), this.code, this.msg)
        is Failure -> this
    }


inline fun <E, T1, T2> Result<E, T1>.flatMap(f: (T1) -> Result<E, T2>): Result<E, T2> =
    when (this) {
        is Success -> f(this.data)
        is Failure -> this
    }


inline fun <E, R, T> Result<E, R>.fold(fl: (E) -> T, fr: (R) -> T): T =
    when (this) {
        is Success -> fr(this.data)
        is Failure -> fl(this.err)
    }


inline fun <E, T> Result<E, T>.getOrElse(f: () -> T): T =
    when (this) {
        is Success -> this.data
        is Failure -> f()
    }
