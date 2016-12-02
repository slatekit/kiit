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


import java.sql._
import scala.reflect.runtime.universe._

import slate.common.{DateTime, Files}


object DbUtils {

  /**
   * gets a new jdbc connection via Driver manager
   * @return
   */
  def connect(con:DbConString) : Connection =
  {
    if(con.driver == "com.mysql.jdbc.Driver")
      DriverManager.getConnection(con.url, con.user, con.password)
    else
      DriverManager.getConnection(con.url)
  }


  /**
    * Execution template providing connection with error-handling and connection closing
    * @param con       : The connection string
    * @param callback  : The callback to call for when the connection is ready
    * @param error     : The callback to call for when an error occurrs
    */
  def execute(con:DbConString, callback:(Connection) => Unit, error:(Exception) => Unit): Unit =
  {
    var conn:Connection = null
    try
    {
      conn = connect(con)
      callback(conn)
    }
    catch
      {
        case ex:Exception =>
        {
          error(ex)
        }
      }
    finally
    {
      close(conn)
    }
  }

  /**
    * Execution template providing connection, statement with error-handling and connection closing
    * @param con       : The connection string
    * @param callback  : The callback to call for when the connection is ready
    * @param error     : The callback to call for when an error occurrs
    */
  def execute(con:DbConString,
              callback:(Connection, Statement) => Unit,
              error:(Exception) => Unit): Unit =
  {
    var conn:Connection = null
    var stmt:Statement = null
    try
    {
      conn = connect(con)
      stmt = conn.createStatement()
      callback(conn, stmt)
    }
    catch
      {
        case ex:Exception =>
        {
          error(ex)
        }
      }
    finally
    {
      close(stmt, conn)
    }
  }


  /**
    * Execution template providing connection, prepared statement with error-handling & conn closing
    * @param con       : The connection string
    * @param sql       : The sql text or stored proc name.
    * @param callback  : The callback to call for when the connection is ready
    * @param error     : The callback to call for when an error occurrs
    */
  def execute(con:DbConString,
              sql:String,
              callback:(Connection, PreparedStatement ) => Unit,
              error:(Exception) => Unit): Unit =
  {
    var conn:Connection = null
    var stmt:CallableStatement = null
    try
    {
      conn = connect(con)
      stmt = conn.prepareCall(sql)
      callback(conn, stmt)
    }
    catch
      {
        case ex:Exception =>
        {
          error(ex)
        }
      }
    finally
    {
      close(stmt, conn)
    }
  }


  /**
    * convenience function to fill prepared statement with parameters
    * @param stmt
    * @param inputs
    */
  def fillArgs(stmt:PreparedStatement, inputs:Option[List[Any]]):Unit = {
    inputs.fold(Unit)( all => {
      var pos = 1
      for(arg <- all){
        arg match {
          case s:String         => stmt.setString (pos, arg.toString)
          case s:Int            => stmt.setInt    (pos, arg.asInstanceOf[Int])
          case s:Long           => stmt.setLong   (pos, arg.asInstanceOf[Long])
          case s:Double         => stmt.setDouble (pos, arg.asInstanceOf[Double])
          case s:Boolean        => stmt.setBoolean(pos, arg.asInstanceOf[Boolean])
          case s:DateTime       => "\"" + s.toString() + "\""
        }
        pos += 1
      }
      Unit
    })
  }


  def getScalar[T](rs:ResultSet, typ:Type): Option[T] = {
    val pos = 1

    if(typ == typeOf[String] )       Option(rs.getString(pos).asInstanceOf[T])
    else if(typ == typeOf[Short]   ) Option(rs.getShort(pos).asInstanceOf[T])
    else if(typ == typeOf[Int]     ) Option(rs.getInt(pos).asInstanceOf[T])
    else if(typ == typeOf[Long]    ) Option(rs.getLong(pos).asInstanceOf[T])
    else if(typ == typeOf[Double]  ) Option(rs.getDouble(pos).asInstanceOf[T])
    else if(typ == typeOf[Boolean] ) Option(rs.getBoolean(pos).asInstanceOf[T])
    else if(typ == typeOf[DateTime]) Option(new DateTime(rs.getTimestamp(pos)).asInstanceOf[T])
    else None
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
}
