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

package slatekit.core.cmds

import slatekit.common.DateTime
import slatekit.results.Failure
import slatekit.results.Result
import slatekit.results.Success

/**
  * The result of command ( cmd ) that was run
  * @param name : Name of the command
  * @param success : Whether it was successful
  * @param message : Message for success/error
  * @param result : A resulting return value
  * @param totalMs : Total time in milliseconds
  * @param started : The start time of the command
  * @param ended : The end time of the command
  */
data class CmdResult(
    val name: String,
    val result: Result<*,*>,
    val started: DateTime,
    val ended: DateTime,
    val totalMs: Long
) {

    val success: Boolean = result.success
    val message: String = result.msg
    val value : Any? = result.getOrNull()
    val error: Throwable? = when( result ) {
        is Success -> null
        is Failure -> result.error as Throwable ?
    }
}