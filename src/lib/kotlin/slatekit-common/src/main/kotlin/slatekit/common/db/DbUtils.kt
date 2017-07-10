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
import java.time.*
import kotlin.reflect.KClass


object DbUtils {

    /**
     * gets a new jdbc connection via Driver manager
     *
     * @return
     */
    fun connect(con: DbCon): Connection =
            if (con.driver == "com.mysql.jdbc.Driver")
                DriverManager.getConnection(con.url, con.user, con.password)
            else
                DriverManager.getConnection(con.url)


    fun close(con: Connection?) = con?.close()


    fun close(stmt: Statement?) = stmt?.close()


    fun close(rs: ResultSet?) = rs?.close()


    /**
     * Execution template providing connection with error-handling and connection closing
     *
     * @param con       : The connection string
     * @param callback  : The callback to call for when the connection is ready
     * @param error     : The callback to call for when an error occurrs
     */
    fun <T> executeCon(con: DbCon, callback: (Connection) -> T, error: (Exception) -> Unit): T? {
        val conn = connect(con)
        val result =
        try {
            conn.use { c ->
                callback(c)
            }
        } catch( ex:Exception ){
            error(ex)
            null
        }
        return result
    }


    /**
     * Execution template providing connection, statement with error-handling and connection closing
     *
     * @param con       : The connection string
     * @param callback  : The callback to call for when the connection is ready
     * @param error     : The callback to call for when an error occurrs
     */
    fun executeStmt(con: DbCon,
                    callback: (Connection, Statement) -> Unit,
                    error: (Exception) -> Unit): Unit {

        val conn = connect(con)
        try {
            conn.use { c ->
                val stmt = c.createStatement()
                stmt.use { s ->
                    callback(c, s)
                }
            }
        } catch( ex:Exception ){
            error(ex)
        }
    }


    /**
     * Execution template providing connection, prepared statement with error-handling & conn closing
     *
     * @param con       : The connection string
     * @param sql       : The sql text or stored proc name.
     * @param callback  : The callback to call for when the connection is ready
     * @param error     : The callback to call for when an error occurrs
     */
    fun <T> executePrepAs(con: DbCon,
                          sql: String,
                          callback: (Connection, PreparedStatement) -> T?,
                          error: (Exception) -> Unit): T? {

        val conn = connect(con)
        val result =
        try {
            conn.use { c ->
                val stmt = c.prepareCall(sql)
                stmt.use { s ->
                    val r = callback(c, s)
                    r
                }
            }
        } catch( ex:Exception ){
            error(ex)
            null
        }
        return result
    }

    /**
     * convenience function to fill prepared statement with parameters
     *
     * @param stmt
     * @param inputs
     */
    fun fillArgs(stmt: PreparedStatement, inputs: List<Any>?): Unit {
        inputs?.forEachIndexed { index, arg ->
            val pos = index + 1
            when (arg.kClass) {
                Types.StringClass        -> stmt.setString(pos, arg.toString())
                Types.BoolClass          -> stmt.setBoolean(pos, arg as Boolean)
                Types.ShortClass         -> stmt.setShort(pos, arg as Short)
                Types.IntClass           -> stmt.setInt(pos, arg as Int)
                Types.LongClass          -> stmt.setLong(pos, arg as Long)
                Types.FloatClass         -> stmt.setFloat(pos, arg as Float)
                Types.DoubleClass        -> stmt.setDouble(pos, arg as Double)
                Types.LocalDateClass     -> stmt.setDate(pos, java.sql.Date.valueOf(arg as LocalDate))
                Types.LocalTimeClass     -> stmt.setTime(pos, java.sql.Time.valueOf(arg as LocalTime))
                Types.LocalDateTimeClass -> stmt.setTimestamp(pos, java.sql.Timestamp.valueOf(arg as LocalDateTime))
                Types.ZonedDateTimeClass -> stmt.setTimestamp(pos, java.sql.Timestamp.valueOf((arg as ZonedDateTime).toLocalDateTime()))
                Types.InstantClass       -> stmt.setTimestamp(pos, java.sql.Timestamp.valueOf(LocalDateTime.ofInstant(arg as Instant, ZoneId.systemDefault())))
                Types.DateTimeClass      -> stmt.setTimestamp(pos, java.sql.Timestamp.valueOf((arg as DateTime).local()))
            }
        }
    }


    @Suppress("UNCHECKED_CAST")
    fun <T> getScalar(rs: ResultSet, typ: KClass<*>): T? {
        val pos = 1

        return if (typ == Types.StringClass) rs.getString(pos) as T
        else if (typ == Types.BoolClass) rs.getBoolean(pos) as T
        else if (typ == Types.ShortClass) rs.getShort(pos) as T
        else if (typ == Types.IntClass) rs.getInt(pos) as T
        else if (typ == Types.LongClass) rs.getLong(pos) as T
        else if (typ == Types.FloatClass) rs.getFloat(pos) as T
        else if (typ == Types.DoubleClass) rs.getDouble(pos) as T
        else if (typ == Types.LocalDateClass) rs.getDate(pos).toLocalDate() as T
        else if (typ == Types.LocalTimeClass) rs.getTime(pos).toLocalTime() as T
        else if (typ == Types.LocalDateTimeClass) rs.getTimestamp(pos).toLocalDateTime() as T
        else if (typ == Types.ZonedDateTimeClass) rs.getTimestamp(pos).toLocalDateTime() as T
        else if (typ == Types.InstantClass) rs.getTimestamp(pos).toInstant() as T
        else if (typ == Types.DateTimeClass) DateTime.of(rs.getTimestamp(pos)) as T
        else null
    }


    fun getTypeFromLang(dataType: KClass<*>):DbFieldType =
            if      (dataType == Types.BoolClass    ) DbFieldTypeBool
            else if (dataType == Types.StringClass  ) DbFieldTypeString
            else if (dataType == Types.ShortClass   ) DbFieldTypeShort
            else if (dataType == Types.IntClass     ) DbFieldTypeNumber
            else if (dataType == Types.LongClass    ) DbFieldTypeLong
            else if (dataType == Types.DoubleClass  ) DbFieldTypeReal
            else if (dataType == Types.LocalDateClass) DbFieldTypeLocalDate
            else if (dataType == Types.LocalTimeClass) DbFieldTypeLocalTime
            else if (dataType == Types.LocalDateTimeClass) DbFieldTypeLocalDateTime
            else if (dataType == Types.ZonedDateTimeClass) DbFieldTypeZonedDateTime
            else if (dataType == Types.InstantClass) DbFieldTypeInstant
            else if (dataType == Types.DateTimeClass) DbFieldTypeDateTime
            else DbFieldTypeString


    fun ensureField(text:String): String =
            if(text.isNullOrEmpty())
                ""
            else {
                text.toLowerCase().trim().filter{ c -> c.isDigit() || c.isLetter() || c == '_'}
            }

}
