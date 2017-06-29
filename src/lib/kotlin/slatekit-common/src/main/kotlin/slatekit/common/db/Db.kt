/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slatekit.common.db


import slatekit.common.Model
import slatekit.common.DateTime
import slatekit.common.db.DbUtils.executeCon
import slatekit.common.db.DbUtils.executePrepAs
import slatekit.common.db.DbUtils.executeStmt
import slatekit.common.db.DbUtils.fillArgs
import slatekit.common.db.types.DbSource
import slatekit.common.db.types.DbSourceMySql
import slatekit.common.mapper.Mapper
import slatekit.common.mapper.MapperSourceRecord
import java.sql.*
import kotlin.reflect.KClass


/**
 * Light-weight database wrapper.
 * @param _dbCon: DbConfig.loadFromUserFolder(".slate", "db.txt")
 *   although tested using mysql, sql-server should be
 * 1. sql-server: driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
 * 2. sql-server: url = "jdbc:sqlserver://<server_name>:<port>;database=<database>;user=<user>;
  * password=<password>;encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
 */
class Db(private val _dbCon:DbCon, val source: DbSource = DbSourceMySql()) {

  /**
    * registers the jdbc driver
 *
    * @return
    */
  fun open(): Db {
    Class.forName(_dbCon.driver)
    return this
  }


  /**
   * creates a table in the database that matches the schema(fields) in the model supplied
 *
   * @param model : The model associated with the table.
   */
  fun createTable(model: Model): Unit {
    val sql = source.builAddTable(model)
    executeStmt(_dbCon, { con, stmt -> stmt.execute(sql) }, this::errorHandler)
  }


  /**
   * drops the table from the database
   *
   * @param name : The name of the table
   */
  fun dropTable(name:String): Unit {
    val sql = source.buildDropTable(name)
    executeStmt(_dbCon, {con, stmt  -> stmt.execute(sql) }, this::errorHandler)
  }


  /**
    * gets a scalar string value using the sql provided
    *
    * @param sql : The sql text
    * @return
    */
  fun <T> getScalar(sql:String, typ:KClass<*>, inputs:List<Any>?): T? =
    getScalarOpt<T>(sql, typ, inputs)


  /**
    * gets a scalar string value using the sql provided
    *
    * @param sql : The sql text
    * @return
    */
  fun <T> getScalarOpt(sql:String, typ:KClass<*>, inputs:List<Any>?): T?  {

    return executePrepAs<T>(_dbCon, sql, { _, stmt ->

      // fill all the arguments into the prepared stmt
      fillArgs(stmt, inputs)

      // execute
      val rs = stmt.executeQuery()
      val any =  rs.next()
      val res:T? =
        if(any) {
          DbUtils.getScalar<T>(rs, typ)
        }
        else
          null
      res
    }, this::errorHandler)
  }


  /**
   * gets a scalar string value using the sql provided
   *
   * @param sql : The sql text
   * @return
   */
  fun getScalarString(sql:String, inputs:List<Any>? = null): String =
    getScalar<String>(sql, slatekit.common.Types.StringClass, inputs) ?: ""


  /**
   * gets a scalar int value using the sql provided
   *
   * @param sql : The sql text
   * @return
   */
  fun getScalarShort(sql:String, inputs:List<Any>? = null): Short =
          getScalar(sql, slatekit.common.Types.ShortClass, inputs) ?: 0.toShort()


  /**
    * gets a scalar int value using the sql provided
    *
    * @param sql : The sql text
    * @return
    */
  fun getScalarInt(sql:String, inputs:List<Any>? = null): Int =
    getScalar(sql, slatekit.common.Types.IntClass, inputs) ?: 0



  /**
    * gets a scalar long value using the sql provided
    *
    * @param sql : The sql text
    * @return
    */
  fun getScalarLong(sql:String, inputs:List<Any>? = null): Long  =
    getScalar(sql, slatekit.common.Types.LongClass, inputs) ?: 0L



  /**
    * gets a scalar double value using the sql provided
    *
    * @param sql : The sql text
    * @return
    */
  fun getScalarFloat(sql:String, inputs:List<Any>? = null): Float =
    getScalar(sql, slatekit.common.Types.FloatClass, inputs) ?: 0.0f


  /**
   * gets a scalar double value using the sql provided
   *
   * @param sql : The sql text
   * @return
   */
  fun getScalarDouble(sql:String, inputs:List<Any>? = null): Double =
          getScalar(sql, slatekit.common.Types.DoubleClass, inputs) ?: 0.0



  /**
    * gets a scalar bool value using the sql provided
    *
    * @param sql : The sql text
    * @return
    */
  fun getScalarBool(sql:String, inputs:List<Any>? = null): Boolean =
    getScalar(sql, slatekit.common.Types.BoolClass, inputs) ?: false



  /**
    * gets a scalar bool value using the sql provided
    *
    * @param sql : The sql text
    * @return
    */
  fun getScalarDate(sql:String, inputs:List<Any>? = null): DateTime =
    getScalar(sql, slatekit.common.Types.DateClass, inputs) ?: DateTime.min()


  /**
   * executes an insert using the sql or stored proc and gets the id
   *
   * @param sql : The sql or stored proc
   * @param inputs  : The inputs for the sql or stored proc
   * @return    : The id ( primary key )
   */
  fun insert(sql:String, inputs:List<Any>? = null):Long {
    val res = executeCon(_dbCon, { con:Connection ->

      val stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)

      // fill all the arguments into the prepared stmt
      fillArgs(stmt, inputs)

      // execute the update
      stmt.executeUpdate()

      // get id.
      val rs = stmt.getGeneratedKeys()
      val id = if (rs.next()){
        rs.getLong(1)
      }
      else
        0L
      DbUtils.close(stmt)
      id
    }, { errorHandler(it )})
    return res ?: 0
  }


  /**
   * executes the update sql or stored proc
   *
   * @param sql     : The sql or stored proc
   * @param inputs  : The inputs for the sql or stored proc
   * @return        : The number of affected records
   */
  fun update(sql:String, inputs:List<Any>? = null):Int {
    val result = executePrepAs<Int>(_dbCon, sql, {con, stmt ->

      // fill all the arguments into the prepared stmt
      fillArgs(stmt, inputs)

      // update and get number of affected records
      val count = stmt.executeUpdate()
      count
    }, { errorHandler(it )})
    return result ?: 0
  }



  /**
   * maps a single item using the sql supplied
   *
   * @param sql    : The sql
   * @param mapper : THe mapper to map the item of type T
   * @tparam T     : The type of the item
   * @return
   */
  @Suppress("UNCHECKED_CAST")
  fun <T> mapOne(sql:String, mapper: Mapper, inputs:List<Any>? = null): T?  {
    val res = executeQuery(sql, { rs ->

      val rec = MapperSourceRecord(rs)
      if(rs.next())
        mapper.mapFrom(rec) as T
      else
        null
    }, false, inputs)
    return res
  }


    /**
     * maps multiple items using the sql supplied
     *
     * @param sql    : The sql
     * @param mapper : THe mapper to map the item of type T
     * @tparam T     : The type of the item
     * @return
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> mapMany(sql:String, mapper:Mapper, inputs:List<Any>? = null): List<T>?  {
      val res = executeQuery(sql, { rs ->

        val rec = MapperSourceRecord(rs)
        val buf = mutableListOf<T>()
        while(rs.next())
        {
          val item = mapper.mapFrom(rec)
          buf.add(item as T )
        }
        buf.toList()
      }, false, inputs)
      return res
    }


  private fun <T> executeQuery(sql:String,
                           callback: (ResultSet) -> T?,
                           moveNext:Boolean = true,
                           inputs:List<Any>? = null): T? {
    val result = executePrepAs<T>(_dbCon, sql, { con:Connection, stmt:PreparedStatement ->

      // fill all the arguments into the prepared stmt
      fillArgs(stmt, inputs)

      // execute
      val rs = stmt.executeQuery()
      
      val any =  if(moveNext) rs.next() else true
      if(any)
        callback(rs)
      else
        null
    }, { errorHandler(it )})
    return result
  }


  fun errorHandler(ex:Exception): Unit
  {
    val msg= ex.message
    println(msg)
  }

  /*
  * DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getUserByEmail`(in email varchar(80))
BEGIN
	select * from `user` where email = email;
END$$
DELIMITER ;
  **/
}
