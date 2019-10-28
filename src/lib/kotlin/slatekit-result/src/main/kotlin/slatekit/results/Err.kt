/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
philosophy: Simplicity above all else
</slate_header>
 */

package slatekit.results

/**
 * Serves as a Marker interface to designate an Error
 * This is used in the [Results].x functions for building Result<T,Err>
 * # Notes
 * 1. This is made flexible to allow clients to represent errors what ever way they like
 * 2. You can create an Err with an exception field/property
 * 3. You can create an Exception that extends from Err
 * 4. You can create an Err that also implements the Status interface to double as a status code
 * 5. You can create an Err using Sealed classes
 */
interface Err {

    val msg: String
    val err: Throwable?
    val ref: Any?

    /**
     *   Here are 3 examples of implementing errors:
     *
     *   ```
     *   // Option 1: Sealed class
     *   sealed class ModelError : Err {
     *           data class CreateError( ... ): ModelError()
     *           // ...
     *   }
     *
     *
     *   // Option 2: Objects
     *   object ModelCreateError : Err { ... }
     *
     *
     *   // Option 3: Exceptions
     *   data class CreateError (override val msg:String, override val code:Int = 8001): Exception(msg), Err
     *   ```
     *   */

    companion object {

        fun of(msg: String): Err {
            return ErrorInfo(msg)
        }

        fun of(status: Status): Err {
            return ErrorInfo(status.msg)
        }

        fun of(ex: Throwable): Err {
            return ErrorInfo(ex.message ?: "", ex)
        }

        fun of(msg: String, ex: Throwable): Err {
            return ErrorInfo(msg, ex)
        }

        fun ex(ex: Exception): Err {
            return ErrorInfo(ex.message ?: "", ex)
        }

        fun obj(err: Any): Err {
            return ErrorInfo(err.toString(), null, err)
        }
    }
}

data class ErrorInfo(override val msg: String, override val err: Throwable? = null, override val ref: Any? = null) : Err
data class ExceptionErr(val msg: String, val err: Err) : Exception(msg)
