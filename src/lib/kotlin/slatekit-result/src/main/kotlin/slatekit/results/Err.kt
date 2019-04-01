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

        /**
         * Converts a simple String message to a instance of [Err] using default implementation [ErrorWithMessage]
         */
        fun of(msg: String, area: String? = null): Err = ErrorWithMessage(msg, area)


        /**
         * Converts an exception into an [Err] using default implementation [ErrorWithException]
         */
        fun of(ex: Exception, area: String? = null): Err = ErrorWithException(ex.message ?: "", ex, area)
    }
}


/**
 * [Err] implemented as a simple message with optional area ( module associated with error )
 * @param msg : String representing error
 * @param area: Area / Module associated with error e.g. "registration"
 * @sample ErrorWithMessage("Duplicate email found", "registration")
 */
data class ErrorWithMessage(val msg: String, val area: String? = null) : Err


/**
 * [Err] implemented with exception with optional area ( module associated with error )
 * @param msg : String representing error
 * @param ex  : Exception
 * @param area: Area / Module associated with error e.g. "registration"
 */
data class ErrorWithException(val msg: String, val ex: Exception, val area: String? = null) : Err


/**
 * [Err] implemented with exception with optional area ( module associated with error )
 * @param msg : String representing error
 * @param target  : [Exception]
 * @param area: Area / Module associated with error e.g. "registration"
 */
data class ErrorWithObject(val msg: String, val target: Any? = null, val area: String? = null) : Err


/**
 * [Err] implemented with exception with optional area ( module associated with error )
 * @param msg : String representing error
 * @param err : Error as an [Err]
 * @param area: Area / Module associated with error e.g. "registration"
 */
data class ExceptionWithErr(val msg: String, val err: Err, val area: String? = null) : Exception(msg)

