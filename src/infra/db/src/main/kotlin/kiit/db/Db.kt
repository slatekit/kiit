/**
 *  <kiit_header>
 * url: www.slatekit.com
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package kiit.db

import kiit.common.values.Record
import kiit.common.conf.Confs
import kiit.common.data.*
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import kiit.common.repeatWith
import kiit.db.DbUtils.executeCall
import kiit.db.DbUtils.executeCon
import kiit.db.DbUtils.executePrep
import kiit.db.DbUtils.executeStmt
import kiit.db.DbUtils.fillArgs

/**
 * Light-weight JDBC based database access wrapper
 * This is used for 2 purposes:
 * 1. Facilitate Unit Testing
 * 2. Facilitate support for the Entities / ORM ( SqlFramework ) project
 *    to abstract away JDBC for Android
 *
 * 1. sql-server: driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
 * 2. sql-server: url = "jdbc:sqlserver://<server_name>:<port>;database=<database>;user=<user>;
 * password=<password>;encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
 */
class Db(private val dbCon: DbCon,
         errorCallback: ((Exception) -> Unit)? = null,
         val settings:DbSettings = DbSettings(true)) : IDb {

    override val errHandler = errorCallback ?: this::errorHandler

    /**
     * Driver name e.g. com.mysql.jdbc.Driver
     */
    override val driver: String = dbCon.driver


    /**
     * registers the jdbc driver
     * @return
     */
    override fun open(): Db {
        Class.forName(dbCon.driver)
        return this
    }

    /**
     * Execute raw sql ( used for DDL )
     */
    override fun execute(sql: String) {
        executeStmt(dbCon, settings, { _, stmt -> stmt.execute(sql) }, errHandler)
    }

    /**
     * executes an insert using the sql or stored proc and gets the id
     *
     * @param sql : The sql or stored proc
     * @param inputs : The inputs for the sql or stored proc
     * @return : The id ( primary key )
     */
    override fun insert(sql: String, inputs: List<Value>?): Long {
        val res = executeCon(dbCon, settings, { con: Connection ->

            val stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            stmt.use { s ->
                // fill all the arguments into the prepared stmt
                inputs?.let { fillArgs(s, inputs, errHandler) }

                // execute the update
                s.executeUpdate()

                // get id.
                val rs = s.generatedKeys
                rs.use { r ->
                    val id = when (r.next()) {
                        true -> r.getLong(1)
                        false -> 0L
                    }
                    id
                }
            }
        }, errHandler)
        return res ?: 0
    }

    /**
     * executes an insert using the sql or stored proc and gets the id
     *
     * @param sql : The sql or stored proc
     * @param inputs : The inputs for the sql or stored proc
     * @return : The id ( primary key )
     */
    override fun insertGetId(sql: String, inputs: List<Value>?): String {
        val res = executeCon(dbCon, settings, { con: Connection ->

            val stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            stmt.use { s ->
                // fill all the arguments into the prepared stmt
                inputs?.let { fillArgs(s, inputs, errHandler) }

                // execute the update
                s.executeUpdate()

                // get id.
                val rs = s.generatedKeys
                rs.use { r ->
                    val id = when (r.next()) {
                        true -> r.getString(1)
                        false -> ""
                    }
                    id
                }
            }
        }, errHandler)
        return res ?: ""
    }

    /**
     * executes the update sql with prepared statement using inputs
     * @param sql : sql statement
     * @param inputs : Inputs for the sql or stored proc
     * @return : The number of affected records
     */
    override fun update(sql: String, inputs: List<Value>?): Int {
        val result = executePrep<Int>(dbCon, settings, sql, { _, stmt ->

            // fill all the arguments into the prepared stmt
            inputs?.let { fillArgs(stmt, inputs, errHandler) }

            // update and get number of affected records
            val count = stmt.executeUpdate()
            count
        }, errHandler)
        return result ?: 0
    }

    /**
     * executes the update sql with prepared statement using inputs
     * @param sql : sql statement
     * @param inputs : Inputs for the sql or stored proc
     * @return : The number of affected records
     */
    override fun call(sql: String, inputs: List<Value>?): Int {
        val result = executeCall<Int>(dbCon, settings, sql, { _, stmt ->

            // fill all the arguments into the prepared stmt
            inputs?.let { fillArgs(stmt, inputs, errHandler) }

            // update and get number of affected records
            val count = stmt.executeUpdate()
            count
        }, errHandler)
        return result ?: 0
    }

    /**
     * gets a scalar string value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    override fun <T> getScalarOrNull(sql: String, typ: DataType, inputs: List<Value>?): T? {

        return executePrep<T>(dbCon, settings, sql, { _, stmt ->

            // fill all the arguments into the prepared stmt
            inputs?.let { fillArgs(stmt, inputs, errHandler) }

            // execute
            val rs = stmt.executeQuery()
            rs.use { r ->
                val res: T? = when (r.next()) {
                    true -> DbUtils.getScalar<T>(r, typ)
                    false -> null
                }
                res
            }
        }, errHandler)
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
        inputs: List<Value>?
    ): T? {
        val result = executePrep<T>(dbCon, settings, sql, { _: Connection, stmt: PreparedStatement ->

            // fill all the arguments into the prepared stmt
            inputs?.let { fillArgs(stmt, inputs, errHandler) }

            // execute
            val rs = stmt.executeQuery()
            rs.use { r ->
                val v = when(if (moveNext) r.next() else true) {
                    true -> callback(r)
                    false -> null
                }
                v
            }
        }, errHandler)
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
    override fun <T> mapOne(sql: String, inputs: List<Value>?, mapper: (Record) -> T?): T? {
        val res = query(sql, { rs ->

            val rec = RecordSet(rs)
            if (rs.next())
                mapper.invoke(rec)
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
    override fun <T> mapAll(sql: String, inputs: List<Value>?, mapper: (Record) -> T?): List<T>? {
        val res = query(sql, { rs ->

            val rec = RecordSet(rs)
            val buf = mutableListOf<T>()
            while (rs.next()) {
                val item = mapper.invoke(rec)
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
        inputs: List<Value>?
    ): T? {

        // {call create_author(?, ?)}
        val holders = inputs?.let { all -> "?".repeatWith(",", all.size) } ?: ""
        val sql = "{call $procName($holders)}"
        return query(sql, callback, moveNext, inputs)
    }

    /**
     * Calls a stored procedure
     * @param procName : The name of the stored procedure e.g. get_by_id
     * @param mapper  : The callback to handle the resultset
     * @param inputs : The parameters for the stored proc. The types will be auto-converted my-sql types.
     */
    override fun <T> callQueryMapped(
            procName: String,
            mapper: (Record) -> T?,
            inputs: List<Value>?
    ): List<T>? {
        // {call create_author(?, ?)}
        val holders = inputs?.let { all -> "?".repeatWith(",", all.size) } ?: ""
        val sql = "{call $procName($holders)}"
        return mapAll(sql, inputs, mapper)
    }

    /**
     * Calls a stored procedure
     * @param procName : The name of the stored procedure e.g. get_by_id
     * @param inputs : The parameters for the stored proc. The types will be auto-converted my-sql types.
     */
    override fun callCreate(procName: String, inputs: List<Value>?): String {
        // {call create_author(?, ?)}
        val holders = inputs?.let { all -> "?".repeatWith(",", all.size) } ?: ""
        val sql = "{call $procName($holders)}"
        return insertGetId(sql, inputs)
    }

    /**
     * Calls a stored procedure
     * @param procName : The name of the stored procedure e.g. get_by_id
     * @param inputs : The parameters for the stored proc. The types will be auto-converted my-sql types.
     */
    override fun callUpdate(procName: String, inputs: List<Value>?): Int {

        // {call create_author(?, ?)}
        val holders = inputs?.let { all -> "?".repeatWith(",", all.size) } ?: ""
        val sql = "{call $procName($holders)}"
        return call(sql, inputs)
    }

    override fun errorHandler(ex: Exception) {
        throw ex
    }


    companion object {

        /**
         * Load Db from config file, which could be a java packaged resource or on file
         * @param cls: Class holding resource files
         * @param path: URI of file
         * 1. "usr://.kiit/common/conf/db.conf"
         * 2. "jar://env.conf
         */
        fun of(cls:Class<*>, path:String):IDb {
            return when(val con = Confs.readDbCon(cls,path)) {
                null -> throw Exception("Unable to load database connection from $path")
                else -> of(con)
            }
        }


        /**
         * Load Db using a default connection from Connections
         * @param cons: Connection collection
         */
        fun of(cons:Connections):IDb {
            return when(val con = cons.default()){
                null -> throw Exception("Unable to load default connection from connections")
                else -> of(con)
            }
        }

        /**
         * Only here for convenience to call open
         */
        fun of(con:DbCon):IDb {
            val db = Db(con)
            if(con != DbCon.empty) db.open()
            return db
        }
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
