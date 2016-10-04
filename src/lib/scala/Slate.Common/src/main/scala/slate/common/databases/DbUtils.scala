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


import java.sql.{Connection, Statement, ResultSet}


object DbUtils {

  def close(con: Connection) =
  {
    if(con != null)
      con.close()
  }


  def close(rs: ResultSet) =
  {
    if(rs != null)
      rs.close()
  }


  def close(stmt:Statement) =
  {
    if(stmt != null)
      stmt.close()
  }


  def close(stmt: Statement, con:Connection): Unit =
  {
    close(stmt)
    close(con)
  }


  def close(rs:ResultSet, stmt: Statement, con:Connection): Unit =
  {
    close(rs)
    close(stmt)
    close(con)
  }
}
