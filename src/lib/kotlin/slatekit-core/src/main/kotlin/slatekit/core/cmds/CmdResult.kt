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
    val success: Boolean,
    val message: String?,
    val error: Throwable?,
    val result: Any?,
    val started: DateTime,
    val ended: DateTime,
    val totalMs: Long
)