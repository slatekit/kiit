/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.db

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import kotlin.io.*
import slatekit.common.db.DbCon
import slatekit.common.db.IDb
import slatekit.common.db.Mapper
import slatekit.common.repeatWith
import slatekit.db.DbUtils.executeCon
import slatekit.db.DbUtils.executePrepAs
import slatekit.db.DbUtils.executeStmt
import slatekit.db.DbUtils.fillArgs
import slatekit.db.types.DbSource
import slatekit.db.types.DbSourceMySql

/**
 * Light-weight database wrapper.
 * @param dbCon: DbConfig.loadFromUserFolder(".slate", "db.txt")
 *   although tested using mysql, sql-server should be
 * 1. sql-server: driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
 * 2. sql-server: url = "jdbc:sqlserver://<server_name>:<port>;database=<database>;user=<user>;
 * password=<password>;encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
 */
class Db(
    private val dbCon: DbCon,
    val source: DbSource = DbSourceMySql(),
    errorCallback: ((Exception) -> Unit)? = null
) : IDb {

    override val onError = errorCallback ?: this::errorHandler

    /**
     * registers the jdbc driver
     *
     * @return
     */
    override fun open(): Db {
        Class.forName(dbCon.driver)
        return this
    }

    override fun execute(sql: String) {
        executeStmt(dbCon, { con, stmt -> stmt.execute(sql) }, onError)
    }

    /**
     * gets a scalar string value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    override fun <T> getScalarOpt(sql: String, typ: Class<*>, inputs: List<Any>?): T? {

        return executePrepAs<T>(dbCon, sql, { _, stmt ->

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
    override fun insert(sql: String, inputs: List<Any>?): Long {
        val res = executeCon(dbCon, { con: Connection ->

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
     * executes an insert using the sql or stored proc and gets the id
     *
     * @param sql : The sql or stored proc
     * @param inputs : The inputs for the sql or stored proc
     * @return : The id ( primary key )
     */
    override fun insertGetId(sql: String, inputs: List<Any>?): String {
        val res = executeCon(dbCon, { con: Connection ->

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
                        r.getString(1)
                    } else
                        ""
                    id
                }
            }
        }, onError)
        return res ?: ""
    }

    /**
     * executes the update sql or stored proc
     *
     * @param sql : The sql or stored proc
     * @param inputs : The inputs for the sql or stored proc
     * @return : The number of affected records
     */
    override fun update(sql: String, inputs: List<Any>?): Int {
        val result = executePrepAs<Int>(dbCon, sql, { con, stmt ->

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
    override fun <T> query(
        sql: String,
        callback: (ResultSet) -> T?,
        moveNext: Boolean,
        inputs: List<Any>?
    ): T? {
        val result = executePrepAs<T>(dbCon, sql, { _: Connection, stmt: PreparedStatement ->

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
    override fun <T> mapOne(sql: String, mapper: Mapper, inputs: List<Any>?): T? {
        val res = query(sql, { rs ->

            val rec = RecordSet(rs)
            if (rs.next())
                mapper.mapFrom<T>(rec)
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
    override fun <T> mapMany(sql: String, mapper: Mapper, inputs: List<Any>?): List<T>? {
        val res = query(sql, { rs ->

            val rec = RecordSet(rs)
            val buf = mutableListOf<T>()
            while (rs.next()) {
                val item = mapper.mapFrom<T>(rec)
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
    override fun <T> callQuery(
        procName: String,
        callback: (ResultSet) -> T?,
        moveNext: Boolean,
        inputs: List<Any>?
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
    override fun <T> callQueryMapped(
        procName: String,
        mapper: Mapper,
        inputs: List<Any>?
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
    override fun callUpdate(procName: String, inputs: List<Any>?): Int {

        // {call create_author(?, ?)}
        val holders = inputs?.let { all -> "?".repeatWith(",", all.size) } ?: ""
        val sql = "{call $procName($holders)}"
        return update(sql, inputs)
    }

    override fun errorHandler(ex: Exception) {
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
