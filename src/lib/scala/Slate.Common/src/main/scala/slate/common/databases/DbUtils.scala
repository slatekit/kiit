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

import slate.common.Files


object DbUtils {


  /**
    * Loads a database config file containing connection properties from a users home directory.
    * The config file is a properties like file with key/value pairs
    *
    *  e.g. on windows {user_home}/.slate/db_default.txt
    *
    *   driver:com.mysql.jdbc.Driver
    *   url:jdbc:mysql://localhost/Database1
    *   user:root
    *   password:123456789
    *
    * @example         : DbConfig.loadFromUserFolder(".slate", "db_default.txt")
    * @param directory : directory name in the users home folder ( e.g. ".slate" )
    * @param fileName  : file name in the directory ( e.g. "db_default.txt" )
    * @return
    */
  def loadFromUserFolder(directory:String, fileName:String): DbConString =
  {
    val configResult = Files.readConfigFromUserFolder(directory, fileName)
    if(configResult.isEmpty)
      throw new IllegalArgumentException(s"database configuration file at $directory, $fileName not found")

    val config = configResult.get

    val con = new DbConString(
      config.getString("db.driver"),
      config.getString("db.url"),
      config.getString("db.user"),
      config.getString("db.password")
    )
    con
  }


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
