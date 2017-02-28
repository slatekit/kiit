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
 *
   * @return
   */
  def connect(con:DbCon) : Connection =
  {
    if(con.driver == "com.mysql.jdbc.Driver")
      DriverManager.getConnection(con.url, con.user, con.password)
    else
      DriverManager.getConnection(con.url)
  }


  /**
    * Execution template providing connection, statement with error-handling and connection closing
 *
    * @param con       : The connection string
    * @param callback  : The callback to call for when the connection is ready
    * @param error     : The callback to call for when an error occurrs
    */
  def execute(con:DbCon,
              callback:(Connection, Statement) => Unit,
              error:(Exception) => Unit): Unit =
  {
    val result = try
    {
      val c = connect(con)
      val s = c.createStatement()
      callback(c, s)
      (Option(c), Option(s))
    }
    catch
      {
        case ex:Exception =>
        {
          error(ex)
          (None, None)
        }
      }
    close(result._2, result._1)
  }


  /**
    * Execution template providing connection with error-handling and connection closing
 *
    * @param con       : The connection string
    * @param callback  : The callback to call for when the connection is ready
    * @param error     : The callback to call for when an error occurrs
    */
  def execute[T](con: DbCon, callback:(Connection) => T, error:(Exception) => Unit): Option[T] =
  {
    val result:(Option[Connection],Option[T]) = try
    {
      val c = connect(con)
      val r = callback(c)
      (Option(c), Option(r))
    }
    catch
      {
        case ex:Exception =>
        {
          error(ex)
          (None, None)
        }
      }
    close(result._1)
    result._2
  }


  /**
    * Execution template providing connection, statement with error-handling and connection closing
 *
    * @param con       : The connection string
    * @param callback  : The callback to call for when the connection is ready
    * @param error     : The callback to call for when an error occurrs
    */
  def executeStmt[T](con:DbCon,
              callback:(Connection, Statement) => T,
              error:(Exception) => Unit): Option[T] =
  {
    val result = try
    {
      val c = connect(con)
      val s = c.createStatement()
      val r = callback(c, s)
      (Option(c), Option(s), Option(r))
    }
    catch
    {
      case ex:Exception =>
      {
        error(ex)
        (None, None, None)
      }
    }
    close(result._2, result._1)
    result._3
  }


  /**
    * Execution template providing connection, prepared statement with error-handling & conn closing
 *
    * @param con       : The connection string
    * @param sql       : The sql text or stored proc name.
    * @param callback  : The callback to call for when the connection is ready
    * @param error     : The callback to call for when an error occurrs
    */
  def executePrepAs[T](con:DbCon,
              sql:String,
              callback:(Connection, PreparedStatement ) => T,
              error:(Exception) => Unit): Option[T] =
  {
    val result = try {
      val c = connect(con)
      val s = c.prepareCall(sql)
      val res = callback(c, s)
      (Option(c), Option(s), Option(res))
    }
    catch
      {
        case ex:Exception =>
        {
          error(ex)
          (None, None, None)
        }
      }
    close(result._2, result._1)
    result._3
  }


  /**
    * convenience function to fill prepared statement with parameters
 *
    * @param stmt
    * @param inputs
    */
  def fillArgs(stmt:PreparedStatement, inputs:Option[List[Any]]):Unit = {
    inputs.fold(Unit)( all => {
      all.indices.foreach( pos => {
        val arg = all(pos)
        arg match {
          case s:String         => stmt.setString (pos, arg.toString)
          case s:Int            => stmt.setInt    (pos, arg.asInstanceOf[Int])
          case s:Long           => stmt.setLong   (pos, arg.asInstanceOf[Long])
          case s:Double         => stmt.setDouble (pos, arg.asInstanceOf[Double])
          case s:Boolean        => stmt.setBoolean(pos, arg.asInstanceOf[Boolean])
          case s:DateTime       => "\"" + s.toString() + "\""
        }
      })
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
    else if(typ == typeOf[DateTime]) Option(DateTime(rs.getTimestamp(pos)).asInstanceOf[T])
    else None
  }


  def close(rs: ResultSet):Unit = Option(rs).fold[Unit](Unit)( r => r.close() )


  def close(con: Connection):Unit = Option(con).fold[Unit](Unit)( c => c.close() )


  def close(con: Option[Connection]):Unit = con.fold[Unit](Unit)( c => c.close() )


  def close(stmt:Statement):Unit = Option(stmt).fold[Unit](Unit)( s => s.close() )


  def close(stmt: Statement, con:Connection): Unit = { close(stmt); close(con); }


  def close(stmt: Option[Statement], con:Option[Connection]): Unit = {
    stmt.foreach( s => close(s) )
    con.foreach( c => close(c) )
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
