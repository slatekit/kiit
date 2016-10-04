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


/**
  * Connection string for a database
  * @param driver   : jdbc driver
  * @param url      : url
  * @param user     : username
  * @param password : password
  */
case class DbConString(driver:String, url:String, user:String, password:String)
{
}


object DbConString {

  val empty:DbConString = new DbConString("", "", "", "")
}
