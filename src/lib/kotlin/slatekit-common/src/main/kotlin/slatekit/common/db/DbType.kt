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


abstract class DbType(val name:String, val driver:String)

object DbTypeMySql     : DbType("mysql"    , "com.mysql.jdbc.Driver")
object DbTypePGres     : DbType("pgres"    , "com.pgres.jdbc.Driver")
object DbTypeMemory    : DbType("memory"   , "com.slatekit.entities.repository-in-memory")

