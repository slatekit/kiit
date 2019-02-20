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

package slatekit.db

import slatekit.common.DateTime
import slatekit.common.db.DbCon
import slatekit.common.db.Mapper
import slatekit.db.DbUtils.executeCon
import slatekit.db.DbUtils.executePrepAs
import slatekit.db.DbUtils.executeStmt
import slatekit.db.DbUtils.fillArgs
import slatekit.db.types.DbSource
import slatekit.db.types.DbSourceMySql
import slatekit.common.repeatWith
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Light-weight database wrapper.
 * @param _dbCon: DbConfig.loadFromUserFolder(".slate", "db.txt")
 *   although tested using mysql, sql-server should be
 * 1. sql-server: driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
 * 2. sql-server: url = "jdbc:sqlserver://<server_name>:<port>;database=<database>;user=<user>;
 * password=<password>;encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
 */
class Db(
        private val _dbCon: DbCon,
        val source: DbSource = DbSourceMySql(),
        val errorCallback: ((Exception) -> Unit)? = null
) {

    val onError = errorCallback ?: this::errorHandler

    /**
     * registers the jdbc driver
     *
     * @return
     */
    fun open(): Db {
        Class.forName(_dbCon.driver)
        return this
    }

    fun execute(sql: String) {
        executeStmt(_dbCon, { con, stmt -> stmt.execute(sql) }, onError)
    }

    /**
     * gets a scalar string value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun <T> getScalar(sql: String, typ: Class<*>, inputs: List<Any>?): T? =
            getScalarOpt<T>(sql, typ, inputs)

    /**
     * gets a scalar string value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarString(sql: String, inputs: List<Any>? = null): String =
            getScalar<String>(sql, slatekit.common.Types.JStringClass, inputs) ?: ""

    /**
     * gets a scalar int value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarShort(sql: String, inputs: List<Any>? = null): Short =
            getScalar(sql, slatekit.common.Types.JShortClass, inputs) ?: 0.toShort()

    /**
     * gets a scalar int value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarInt(sql: String, inputs: List<Any>? = null): Int =
            getScalar(sql, slatekit.common.Types.JIntClass, inputs) ?: 0

    /**
     * gets a scalar long value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarLong(sql: String, inputs: List<Any>? = null): Long =
            getScalar(sql, slatekit.common.Types.JLongClass, inputs) ?: 0L

    /**
     * gets a scalar double value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarFloat(sql: String, inputs: List<Any>? = null): Float =
            getScalar(sql, slatekit.common.Types.JFloatClass, inputs) ?: 0.0f

    /**
     * gets a scalar double value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarDouble(sql: String, inputs: List<Any>? = null): Double =
            getScalar(sql, slatekit.common.Types.JDoubleClass, inputs) ?: 0.0

    /**
     * gets a scalar bool value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarBool(sql: String, inputs: List<Any>? = null): Boolean =
            getScalar(sql, slatekit.common.Types.JBoolClass, inputs) ?: false

    /**
     * gets a scalar local date value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarLocalDate(sql: String, inputs: List<Any>? = null): LocalDate =
            getScalar(sql, slatekit.common.Types.JLocalDateClass, inputs) ?: LocalDate.MIN

    /**
     * gets a scalar local time value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarLocalTime(sql: String, inputs: List<Any>? = null): LocalTime =
            getScalar(sql, slatekit.common.Types.JLocalTimeClass, inputs) ?: LocalTime.MIN

    /**
     * gets a scalar local datetime value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarLocalDateTime(sql: String, inputs: List<Any>? = null): LocalDateTime =
            getScalar(sql, slatekit.common.Types.JLocalDateTimeClass, inputs) ?: LocalDateTime.MIN

    /**
     * gets a scalar local datetime value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarDate(sql: String, inputs: List<Any>? = null): DateTime =
            getScalar(sql, slatekit.common.Types.JDateTimeClass, inputs) ?: DateTime.MIN

    /**
     * gets a scalar string value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun <T> getScalarOpt(sql: String, typ: Class<*>, inputs: List<Any>?): T? {

        return executePrepAs<T>(_dbCon, sql, { _, stmt ->

            // fill all the arguments into the prepared stmt
            inputs?.let { fillArgs(stmt, inputs) }

            // execute
            val rs = stmt.executeQuery()
            rs.use { r ->
                val any = r.next()
                val res: T? =
                        if (any) {
                            DbUtils.getScalar<T>(r, typ)
                        } else
                            null
                res
            }
        }, onError)
    }

    /**
     * executes an insert using the sql or stored proc and gets the id
     *
     * @param sql : The sql or stored proc
     * @param inputs : The inputs for the sql or stored proc
     * @return : The id ( primary key )
     */
    fun insert(sql: String, inputs: List<Any>? = null): Long {
        val res = executeCon(_dbCon, { con: Connection ->

            val stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            stmt.use { s ->
                // fill all the arguments into the prepared stmt
                inputs?.let { fillArgs(s, inputs) }

                // execute the update
                s.executeUpdate()

                // get id.
                val rs = s.generatedKeys
                rs.use { r ->
                    val id = if (r.next()) {
                        r.getLong(1)
                    } else
                        0L
                    id
                }
            }
        }, onError)
        return res ?: 0
    }

    /**
     * executes the update sql or stored proc
     *
     * @param sql : The sql or stored proc
     * @param inputs : The inputs for the sql or stored proc
     * @return : The number of affected records
     */
    fun update(sql: String, inputs: List<Any>? = null): Int {
        val result = executePrepAs<Int>(_dbCon, sql, { con, stmt ->

            // fill all the arguments into the prepared stmt
            inputs?.let { fillArgs(stmt, inputs) }

            // update and get number of affected records
            val count = stmt.executeUpdate()
            count
        }, onError)
        return result ?: 0
    }

    /**
     * Executes a sql query
     * @param sql : The sql to query
     * @param callback : The callback to handle the resultset
     * @param moveNext : Whether or not to automatically move the resultset to the next/first row
     * @param inputs : The parameters for the stored proc. The types will be auto-converted my-sql types.
     */
    fun <T> query(
        sql: String,
        callback: (ResultSet) -> T?,
        moveNext: Boolean = true,
        inputs: List<Any>? = null
    ): T? {
        val result = executePrepAs<T>(_dbCon, sql, { _: Connection, stmt: PreparedStatement ->

            // fill all the arguments into the prepared stmt
            inputs?.let { fillArgs(stmt, inputs) }

            // execute
            val rs = stmt.executeQuery()
            rs.use { r ->
                val any = if (moveNext) r.next() else true
                if (any)
                    callback(r)
                else
                    null
            }
        }, onError)
        return result
    }

    /**
     * maps a single item using the sql supplied
     *
     * @param sql : The sql
     * @param mapper : THe mapper to map the item of type T
     * @tparam T     : The type of the item
     * @return
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> mapOne(sql: String, mapper: Mapper, inputs: List<Any>? = null): T? {
        val res = query(sql, { rs ->

            val rec = RecordSet(rs)
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
     * @param sql : The sql
     * @param mapper : THe mapper to map the item of type T
     * @tparam T     : The type of the item
     * @return
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> mapMany(sql: String, mapper: Mapper, inputs: List<Any>? = null): List<T>? {
        val res = query(sql, { rs ->

            val rec = RecordSet(rs)
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
     * @param inputs : The parameters for the stored proc. The types will be auto-converted my-sql types.
     */
    fun <T> callQuery(
        procName: String,
        callback: (ResultSet) -> T?,
        moveNext: Boolean = true,
        inputs: List<Any>? = null
    ): T? {

        // {call create_author(?, ?)}
        val holders = inputs?.let { all -> "?".repeatWith(",", all.size) } ?: ""
        val sql = "{call $procName($holders)}"
        return query(sql, callback, moveNext, inputs)
    }

    /**
     * Calls a stored procedure
     * @param procName : The name of the stored procedure e.g. get_by_id
     * @param callback : The callback to handle the resultset
     * @param moveNext : Whether or not to automatically move the resultset to the next/first row
     * @param inputs : The parameters for the stored proc. The types will be auto-converted my-sql types.
     */
    fun <T> callQueryMapped(
            procName: String,
            mapper: Mapper,
            inputs: List<Any>? = null
    ): List<T>? {

        // {call create_author(?, ?)}
        val holders = inputs?.let { all -> "?".repeatWith(",", all.size) } ?: ""
        val sql = "{call $procName($holders)}"
        return mapMany(sql, mapper, inputs)
    }

    /**
     * Calls a stored procedure
     * @param procName : The name of the stored procedure e.g. get_by_id
     * @param callback : The callback to handle the resultset
     * @param moveNext : Whether or not to automatically move the resultset to the next/first row
     * @param inputs : The parameters for the stored proc. The types will be auto-converted my-sql types.
     */
    fun callUpdate(procName: String, inputs: List<Any>? = null): Int {

        // {call create_author(?, ?)}
        val holders = inputs?.let { all -> "?".repeatWith(",", all.size) } ?: ""
        val sql = "{call $procName($holders)}"
        return update(sql, inputs)
    }

    fun errorHandler(ex: Exception) {
        println("Database error : " + ex.message)
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
