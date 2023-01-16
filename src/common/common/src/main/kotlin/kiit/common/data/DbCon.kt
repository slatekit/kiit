/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *
 * 
  *  </kiit_header>
 */

package kiit.common.data

interface DbCon {
  val driver: String
  val url: String
  val user: String
  val pswd: String

  companion object {
    @JvmField val empty = DbConString("", "", "", "")
  }
}


/**
  * Connection string for a database
  * @param driver : jdbc driver
  * @param url : url
  * @param user : username
  * @param pswd : password
  */
data class DbConString(
    override val driver: String,
    override val url: String,
    override val user: String,
    override val pswd: String
) : DbCon {
  constructor(vendor: Vendor, url:String, user:String, pswd:String) : this(vendor.driver, url, user, pswd)
}

