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

package slatekit.common.db

interface DbCon {
  val driver: String
  val url: String
  val user: String
  val password: String

  companion object {

    @JvmField val empty = DbConString("", "", "", "")
  }
}

/**
  * Connection string for a database
  * @param driver : jdbc driver
  * @param url : url
  * @param user : username
  * @param password : password
  */
data class DbConString(
    override val driver: String,
    override val url: String,
    override val user: String,
    override val password: String
) : DbCon

