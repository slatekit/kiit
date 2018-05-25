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
import java.sql.*
import java.time.*


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
            val jcls = arg.javaClass
            when (jcls) {
                Types.JStringAnyClass        -> stmt.setString(pos, arg.toString())
                Types.JBoolAnyClass          -> stmt.setBoolean(pos, arg as Boolean)
                Types.JShortAnyClass         -> stmt.setShort(pos, arg as Short)
                Types.JIntAnyClass           -> stmt.setInt(pos, arg as Int)
                Types.JLongAnyClass          -> stmt.setLong(pos, arg as Long)
                Types.JFloatAnyClass         -> stmt.setFloat(pos, arg as Float)
                Types.JDoubleAnyClass        -> stmt.setDouble(pos, arg as Double)
                Types.JLocalDateAnyClass     -> stmt.setDate(pos, java.sql.Date.valueOf(arg as LocalDate))
                Types.JLocalTimeAnyClass     -> stmt.setTime(pos, java.sql.Time.valueOf(arg as LocalTime))
                Types.JLocalDateTimeAnyClass -> stmt.setTimestamp(pos, java.sql.Timestamp.valueOf(arg as LocalDateTime))
                Types.JZonedDateTimeAnyClass -> stmt.setTimestamp(pos, java.sql.Timestamp.valueOf((arg as ZonedDateTime).toLocalDateTime()))
                Types.JInstantAnyClass       -> stmt.setTimestamp(pos, java.sql.Timestamp.valueOf(LocalDateTime.ofInstant(arg as Instant, ZoneId.systemDefault())))
                Types.JDateTimeAnyClass      -> stmt.setTimestamp(pos, java.sql.Timestamp.valueOf((arg as DateTime).local()))
            }
        }
    }


    @Suppress("UNCHECKED_CAST")
    fun <T> getScalar(rs: ResultSet, typ: Class<*>): T? {
        val pos = 1

        return if (typ == Types.JStringClass) rs.getString(pos) as T
        else if   (typ == Types.JBoolClass) rs.getBoolean(pos) as T
        else if   (typ == Types.JShortClass) rs.getShort(pos) as T
        else if   (typ == Types.JIntClass) rs.getInt(pos) as T
        else if   (typ == Types.JLongClass) rs.getLong(pos) as T
        else if   (typ == Types.JFloatClass) rs.getFloat(pos) as T
        else if   (typ == Types.JDoubleClass) rs.getDouble(pos) as T
        else if   (typ == Types.JLocalDateClass) rs.getDate(pos).toLocalDate() as T
        else if   (typ == Types.JLocalTimeClass) rs.getTime(pos).toLocalTime() as T
        else if   (typ == Types.JLocalDateTimeClass) rs.getTimestamp(pos).toLocalDateTime() as T
        else if   (typ == Types.JZonedDateTimeClass) rs.getTimestamp(pos).toLocalDateTime() as T
        else if   (typ == Types.JInstantClass) rs.getTimestamp(pos).toInstant() as T
        else if   (typ == Types.JDateTimeClass) DateTime.of(rs.getTimestamp(pos)) as T
        else null
    }


    fun getTypeFromLang(dataType: Class<*>):DbFieldType =
            if      (dataType == Types.JBoolClass    ) DbFieldTypeBool
            else if (dataType == Types.JStringClass  ) DbFieldTypeString
            else if (dataType == Types.JShortClass   ) DbFieldTypeShort
            else if (dataType == Types.JIntClass     ) DbFieldTypeNumber
            else if (dataType == Types.JLongClass    ) DbFieldTypeLong
            else if (dataType == Types.JDoubleClass  ) DbFieldTypeReal
            else if (dataType == Types.JLocalDateClass) DbFieldTypeLocalDate
            else if (dataType == Types.JLocalTimeClass) DbFieldTypeLocalTime
            else if (dataType == Types.JLocalDateTimeClass) DbFieldTypeLocalDateTime
            else if (dataType == Types.JZonedDateTimeClass) DbFieldTypeZonedDateTime
            else if (dataType == Types.JInstantClass) DbFieldTypeInstant
            else if (dataType == Types.JDateTimeClass) DbFieldTypeDateTime
            else DbFieldTypeString


    fun ensureField(text:String): String =
            if(text.isNullOrEmpty())
                ""
            else {
                text.trim().filter{ c -> c.isDigit() || c.isLetter() || c == '_'}
            }

}
