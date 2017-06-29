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


import slatekit.common.DateTime
import slatekit.common.Types
import slatekit.common.kClass
import java.sql.*
import kotlin.reflect.KClass


object DbUtils {

  /**
   * gets a new jdbc connection via Driver manager
   *
   * @return
   */
  fun connect(con:DbCon) : Connection =
    if(con.driver == "com.mysql.jdbc.Driver")
      DriverManager.getConnection(con.url, con.user, con.password)
    else
      DriverManager.getConnection(con.url)


  fun close(con: Connection?) = con?.close()


  fun close(stmt:Statement?) = stmt?.close()


  fun close(rs: ResultSet?) = rs?.close()


  fun close(stmt: Statement?, con:Connection?) {
    close(stmt)
    close(con)
  }


  fun close(rs:ResultSet, stmt: Statement, con:Connection) {
    close(rs)
    close(stmt)
    close(con)
  }


  /**
    * Execution template providing connection with error-handling and connection closing
    *
    * @param con       : The connection string
    * @param callback  : The callback to call for when the connection is ready
    * @param error     : The callback to call for when an error occurrs
    */
  fun <T> executeCon(con: DbCon, callback:(Connection) -> T, error:(Exception) -> Unit): T?
  {
    val result = try
    {
      val c = connect(con)
      val r = callback(c)
      Pair(c, r)
    }
    catch(ex:Exception )
    {
      error(ex)
      Pair(null, null)
    }
    close(result.first)
    return result.second
  }


  /**
   * Execution template providing connection, statement with error-handling and connection closing
   *
   * @param con       : The connection string
   * @param callback  : The callback to call for when the connection is ready
   * @param error     : The callback to call for when an error occurrs
   */
  fun executeStmt(con:DbCon,
                  callback:(Connection, Statement) -> Unit,
                  error:(Exception) -> Unit):Unit {
    val result = try
    {
      val c = connect(con)
      val s = c.createStatement()
      callback(c, s)
      Pair(c, s)
    }
    catch(ex:Exception)
    {
      error(ex)
      Pair(null, null)
    }
    close(result.second, result.first)
  }


  /**
    * Execution template providing connection, statement with error-handling and connection closing
    *
    * @param con       : The connection string
    * @param callback  : The callback to call for when the connection is ready
    * @param error     : The callback to call for when an error occurrs
    */
  fun <T> executeStmtWithResult(con:DbCon,
              callback:(Connection, Statement) -> T,
              error:(Exception) -> Unit): T?
  {
    val result = try
    {
      val c = connect(con)
      val s = c.createStatement()
      val r = callback(c, s)
      Triple(c, s, r)
    }
    catch(ex:Exception )
    {
      error(ex)
      Triple(null, null, null)
    }
    close(result.second, result.first)
    return result.third
  }



  /**
    * Execution template providing connection, prepared statement with error-handling & conn closing
 *
    * @param con       : The connection string
    * @param sql       : The sql text or stored proc name.
    * @param callback  : The callback to call for when the connection is ready
    * @param error     : The callback to call for when an error occurrs
    */
  fun <T> executePrepAs(con:DbCon,
              sql:String,
              callback:(Connection, PreparedStatement ) -> T?,
              error:(Exception) -> Unit): T?
  {
    val result = try {
      val c = connect(con)
      val s = c.prepareCall(sql)
      val r = callback(c, s)
      Triple(c, s, r)
    }
    catch(ex:Exception )
    {
      error(ex)
      Triple(null, null, null)
    }
    close(result.second, result.first)
    return result.third
  }

  /**
    * convenience function to fill prepared statement with parameters
 *
    * @param stmt
    * @param inputs
    */
  fun fillArgs(stmt:PreparedStatement, inputs:List<Any>?):Unit {
    inputs?.forEachIndexed { pos, arg ->
      when ( arg.kClass ) {
        Types.StringClass     -> stmt.setString (pos, arg.toString())
        Types.BoolClass       -> stmt.setBoolean(pos, arg as Boolean)
        Types.ShortClass      -> stmt.setShort  (pos, arg as Short)
        Types.IntClass        -> stmt.setInt    (pos, arg as Int)
        Types.LongClass       -> stmt.setLong   (pos, arg as Long)
        Types.FloatClass      -> stmt.setFloat  (pos, arg as Float)
        Types.DoubleClass     -> stmt.setDouble (pos, arg as Double)
        Types.DateClass       -> "\"" + arg.toString() + "\""
      }
    }
  }

  @Suppress("UNCHECKED_CAST")
  fun <T> getScalar(rs:ResultSet, typ: KClass<*>): T? {
    val pos = 1

    return if ( typ == Types.StringClass ) rs.getString(pos)  as T
    else if ( typ == Types.BoolClass   ) rs.getBoolean(pos)  as T
    else if ( typ == Types.ShortClass  ) rs.getShort(pos)    as T
    else if ( typ == Types.IntClass    ) rs.getInt(pos)     as T
    else if ( typ == Types.LongClass   ) rs.getLong(pos)  as T
    else if ( typ == Types.FloatClass  ) rs.getFloat(pos)   as T
    else if ( typ == Types.DoubleClass ) rs.getDouble(pos) as T
    else if ( typ == Types.DateClass   ) DateTime(rs.getTimestamp(pos)) as T
    else null
  }
}
