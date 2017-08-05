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


import slatekit.common.DateTime
import slatekit.common.Model
import slatekit.common.db.DbUtils.executeCon
import slatekit.common.db.DbUtils.executePrepAs
import slatekit.common.db.DbUtils.executeStmt
import slatekit.common.db.DbUtils.fillArgs
import slatekit.common.db.types.DbSource
import slatekit.common.db.types.DbSourceMySql
import slatekit.common.mapper.Mapper
import slatekit.common.mapper.MapperSourceRecord
import slatekit.common.repeatWith
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.reflect.KClass


/**
 * Light-weight database wrapper.
 * @param _dbCon: DbConfig.loadFromUserFolder(".slate", "db.txt")
 *   although tested using mysql, sql-server should be
 * 1. sql-server: driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
 * 2. sql-server: url = "jdbc:sqlserver://<server_name>:<port>;database=<database>;user=<user>;
 * password=<password>;encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
 */
class Db(private val _dbCon: DbCon, val source: DbSource = DbSourceMySql()) {

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
    fun dropTable(name: String): Unit {
        val sql = source.buildDropTable(name)
        executeStmt(_dbCon, { con, stmt -> stmt.execute(sql) }, this::errorHandler)
    }


    /**
     * gets a scalar string value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun <T> getScalar(sql: String, typ: KClass<*>, inputs: List<Any>?): T? =
            getScalarOpt<T>(sql, typ, inputs)


    /**
     * gets a scalar string value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarString(sql: String, inputs: List<Any>? = null): String =
            getScalar<String>(sql, slatekit.common.Types.StringClass, inputs) ?: ""


    /**
     * gets a scalar int value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarShort(sql: String, inputs: List<Any>? = null): Short =
            getScalar(sql, slatekit.common.Types.ShortClass, inputs) ?: 0.toShort()


    /**
     * gets a scalar int value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarInt(sql: String, inputs: List<Any>? = null): Int =
            getScalar(sql, slatekit.common.Types.IntClass, inputs) ?: 0


    /**
     * gets a scalar long value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarLong(sql: String, inputs: List<Any>? = null): Long =
            getScalar(sql, slatekit.common.Types.LongClass, inputs) ?: 0L


    /**
     * gets a scalar double value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarFloat(sql: String, inputs: List<Any>? = null): Float =
            getScalar(sql, slatekit.common.Types.FloatClass, inputs) ?: 0.0f


    /**
     * gets a scalar double value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarDouble(sql: String, inputs: List<Any>? = null): Double =
            getScalar(sql, slatekit.common.Types.DoubleClass, inputs) ?: 0.0


    /**
     * gets a scalar bool value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarBool(sql: String, inputs: List<Any>? = null): Boolean =
            getScalar(sql, slatekit.common.Types.BoolClass, inputs) ?: false


    /**
     * gets a scalar local date value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarLocalDate(sql: String, inputs: List<Any>? = null): LocalDate =
            getScalar(sql, slatekit.common.Types.LocalDateClass, inputs) ?: LocalDate.MIN


    /**
     * gets a scalar local time value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarLocalTime(sql: String, inputs: List<Any>? = null): LocalTime =
            getScalar(sql, slatekit.common.Types.LocalTimeClass, inputs) ?: LocalTime.MIN


    /**
     * gets a scalar local datetime value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarLocalDateTime(sql: String, inputs: List<Any>? = null): LocalDateTime =
            getScalar(sql, slatekit.common.Types.LocalDateTimeClass, inputs) ?: LocalDateTime.MIN


    /**
     * gets a scalar local datetime value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarDate(sql: String, inputs: List<Any>? = null): DateTime =
            getScalar(sql, slatekit.common.Types.DateTimeClass, inputs) ?: DateTime.MIN


    /**
     * gets a scalar string value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun <T> getScalarOpt(sql: String, typ: KClass<*>, inputs: List<Any>?): T? {

        return executePrepAs<T>(_dbCon, sql, { _, stmt ->

            // fill all the arguments into the prepared stmt
            fillArgs(stmt, inputs)

            // execute
            val rs = stmt.executeQuery()
            rs.use { r ->
                val any = r.next()
                val res: T? =
                        if (any) {
                            DbUtils.getScalar<T>(r, typ)
                        }
                        else
                            null
                res
            }
        }, this::errorHandler)
    }


    /**
     * executes an insert using the sql or stored proc and gets the id
     *
     * @param sql : The sql or stored proc
     * @param inputs  : The inputs for the sql or stored proc
     * @return    : The id ( primary key )
     */
    fun insert(sql: String, inputs: List<Any>? = null): Long {
        val res = executeCon(_dbCon, { con: Connection ->

            val stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            stmt.use { s ->
                // fill all the arguments into the prepared stmt
                fillArgs(s, inputs)

                // execute the update
                s.executeUpdate()

                // get id.
                val rs = s.generatedKeys
                rs.use { r ->
                    val id = if (r.next()) {
                        r.getLong(1)
                    }
                    else
                        0L
                    id
                }
            }
        }, this::errorHandler)
        return res ?: 0
    }


    /**
     * executes the update sql or stored proc
     *
     * @param sql     : The sql or stored proc
     * @param inputs  : The inputs for the sql or stored proc
     * @return        : The number of affected records
     */
    fun update(sql: String, inputs: List<Any>? = null): Int {
        val result = executePrepAs<Int>(_dbCon, sql, { con, stmt ->

            // fill all the arguments into the prepared stmt
            fillArgs(stmt, inputs)

            // update and get number of affected records
            val count = stmt.executeUpdate()
            count
        }, { errorHandler(it) })
        return result ?: 0
    }


    /**
     * Executes a sql query
     * @param sql      : The sql to query
     * @param callback : The callback to handle the resultset
     * @param moveNext : Whether or not to automatically move the resultset to the next/first row
     * @param inputs   : The parameters for the stored proc. The types will be auto-converted my-sql types.
     */
    fun <T> query(sql: String,
                  callback: (ResultSet) -> T?,
                  moveNext: Boolean = true,
                  inputs: List<Any>? = null): T? {
        val result = executePrepAs<T>(_dbCon, sql, { _: Connection, stmt: PreparedStatement ->

            // fill all the arguments into the prepared stmt
            fillArgs(stmt, inputs)

            // execute
            val rs = stmt.executeQuery()
            rs.use { r ->
                val any = if (moveNext) r.next() else true
                if (any)
                    callback(r)
                else
                    null
            }
        }, this::errorHandler)
        return result
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
    fun <T> mapOne(sql: String, mapper: Mapper, inputs: List<Any>? = null): T? {
        val res = query(sql, { rs ->

            val rec = MapperSourceRecord(rs)
            if (rs.next())
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
    fun <T> mapMany(sql: String, mapper: Mapper, inputs: List<Any>? = null): List<T>? {
        val res = query(sql, { rs ->

            val rec = MapperSourceRecord(rs)
            val buf = mutableListOf<T>()
            while (rs.next()) {
                val item = mapper.mapFrom(rec)
                buf.add(item as T)
            }
            buf.toList()
        }, false, inputs)
        return res
    }


    /**
     * Calls a stored procedure
     * @param procName : The name of the stored procedure e.g. get_by_id
     * @param callback : The callback to handle the resultset
     * @param moveNext : Whether or not to automatically move the resultset to the next/first row
     * @param inputs   : The parameters for the stored proc. The types will be auto-converted my-sql types.
     */
    fun <T> callQuery(procName: String,
                 callback: (ResultSet) -> T?,
                 moveNext: Boolean = true,
                 inputs  : List<Any>? = null): T? {

        // {call create_author(?, ?)}
        val holders = inputs?.let{ all -> "?".repeatWith(",", all.size) } ?: ""
        val sql = "{call $procName($holders)}"
        return query(sql, callback, moveNext, inputs)
    }


    /**
     * Calls a stored procedure
     * @param procName : The name of the stored procedure e.g. get_by_id
     * @param callback : The callback to handle the resultset
     * @param moveNext : Whether or not to automatically move the resultset to the next/first row
     * @param inputs   : The parameters for the stored proc. The types will be auto-converted my-sql types.
     */
    fun <T> callQueryMapped(procName: String,
                            mapper  : Mapper,
                            inputs  : List<Any>? = null): List<T>? {

        // {call create_author(?, ?)}
        val holders = inputs?.let{ all -> "?".repeatWith(",", all.size) } ?: ""
        val sql = "{call $procName($holders)}"
        return mapMany(sql, mapper, inputs)
    }


    /**
     * Calls a stored procedure
     * @param procName : The name of the stored procedure e.g. get_by_id
     * @param callback : The callback to handle the resultset
     * @param moveNext : Whether or not to automatically move the resultset to the next/first row
     * @param inputs   : The parameters for the stored proc. The types will be auto-converted my-sql types.
     */
    fun callUpdate(procName: String, inputs: List<Any>? = null): Int {

        // {call create_author(?, ?)}
        val holders = inputs?.let{ all -> "?".repeatWith(",", all.size) } ?: ""
        val sql = "{call $procName($holders)}"
        return update(sql, inputs)
    }


    fun errorHandler(ex: Exception): Unit {
        val msg = ex.message
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
