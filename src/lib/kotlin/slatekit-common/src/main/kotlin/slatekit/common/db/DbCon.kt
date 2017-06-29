/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2016 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slatekit.common.db

interface DbCon {
  val driver:String
  val url:String
  val user:String
  val password:String
}


/**
  * Connection string for a database
  * @param driver   : jdbc driver
  * @param url      : url
  * @param user     : username
  * @param password : password
  */
data class DbConString( override val driver:String,
                        override val url:String,
                        override val user:String,
                        override val password:String) : DbCon


/**
  * Empty connection string
  */
object DbConEmpty : DbCon {
  override val driver:String   = ""
  override val url:String      = ""
  override val user:String     = ""
  override val password:String = ""
}
