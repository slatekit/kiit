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


abstract class DbType(val name:String, val driver:String)

case object DbTypeMySql     extends DbType("mysql"    , "com.mysql.jdbc.Driver")
case object DbTypeSqlServer extends DbType("sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver")
case object DbTypeMemory    extends DbType("memory"   , "com.slatekit.entities.repository-in-memory")

