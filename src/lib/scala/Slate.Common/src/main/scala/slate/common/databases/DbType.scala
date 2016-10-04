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


sealed class DbType(val name:String, val driver:String)


/**
  * Container for the different database types.
  */
object DbType {

  object Mysql     extends DbType("mysql"    , "com.mysql.jdbc.Driver")
  object SqlServer extends DbType("sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver")
}
