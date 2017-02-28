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

package slate.common.databases


trait DbCon {
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
final case class DbConString(driver:String, url:String, user:String, password:String)
  extends DbCon
{
}


/**
  * Empty connection string
  */
case object DbConEmpty extends DbCon {
  val driver:String   = ""
  val url:String      = ""
  val user:String     = ""
  val password:String = ""
}
