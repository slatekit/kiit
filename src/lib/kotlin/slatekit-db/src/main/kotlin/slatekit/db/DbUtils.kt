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

import slatekit.common.DateTime
import slatekit.common.Types
import slatekit.common.data.DbCon
import java.sql.*
import org.threeten.bp.*
import slatekit.common.DateTimes
import slatekit.common.data.DataType
import slatekit.common.ext.local

object DbUtils {

    /**
     * gets a new jdbc connection via Driver manager
     *
     * @return
     */
    fun connect(con: DbCon): Connection =
            if (con.driver == "com.mysql.jdbc.Driver")
                DriverManager.getConnection(con.url, con.user, con.pswd)
            else
                DriverManager.getConnection(con.url)

    /**
     * Execution template providing connection with error-handling and connection closing
     *
     * @param con : The connection string
     * @param callback : The callback to call for when the connection is ready
     * @param error : The callback to call for when an error occurrs
     */
    fun <T> executeCon(con: DbCon, callback: (Connection) -> T, error: (Exception) -> Unit): T? {
        val conn = connect(con)
        val result =
        try {
            conn.use { c ->
                callback(c)
            }
        } catch (ex: Exception) {
            error(ex)
            null
        }
        return result
    }

    /**
     * Execution template providing connection, statement with error-handling and connection closing
     *
     * @param con : The connection string
     * @param callback : The callback to call for when the connection is ready
     * @param error : The callback to call for when an error occurrs
     */
    fun executeStmt(
        con: DbCon,
        callback: (Connection, Statement) -> Unit,
        error: (Exception) -> Unit
    ) {

        val conn = connect(con)
        try {
            conn.use { c ->
                val stmt = c.createStatement()
                stmt.use { s ->
                    callback(c, s)
                }
            }
        } catch (ex: Exception) {
            error(ex)
        }
    }

    /**
     * Execution template providing connection, prepared statement with error-handling & conn closing
     *
     * @param con : The connection string
     * @param sql : The sql text or stored proc name.
     * @param callback : The callback to call for when the connection is ready
     * @param error : The callback to call for when an error occurrs
     */
    fun <T> executePrep(
        con: DbCon,
        sql: String,
        callback: (Connection, PreparedStatement) -> T?,
        error: (Exception) -> Unit
    ): T? {

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
        } catch (ex: Exception) {
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
    fun fillArgs(stmt: PreparedStatement, inputs: List<Any?>?) {
        inputs?.forEachIndexed { index, arg ->
            val pos = index + 1
            when(arg) {
                null -> stmt.setNull(pos, 0)
                else -> {
                    val jcls = arg.javaClass
                    when (jcls) {
                        Types.JStringAnyClass -> stmt.setString(pos, arg.toString())
                        Types.JBoolAnyClass -> stmt.setBoolean(pos, arg as Boolean)
                        Types.JShortAnyClass -> stmt.setShort(pos, arg as Short)
                        Types.JIntAnyClass -> stmt.setInt(pos, arg as Int)
                        Types.JLongAnyClass -> stmt.setLong(pos, arg as Long)
                        Types.JFloatAnyClass -> stmt.setFloat(pos, arg as Float)
                        Types.JDoubleAnyClass -> stmt.setDouble(pos, arg as Double)
                        // Types.JDecimalClass -> stmt.setBigDecimal(pos, arg as BigDecimal)
                        Types.JLocalDateAnyClass -> stmt.setDate(pos, java.sql.Date.valueOf((arg as LocalDate).toJava8LocalDate()))
                        Types.JLocalTimeAnyClass -> stmt.setTime(pos, java.sql.Time.valueOf((arg as LocalTime).toJava8LocalTime()))
                        Types.JLocalDateTimeAnyClass -> stmt.setTimestamp(pos, java.sql.Timestamp.valueOf((arg as LocalDateTime).toJava8LocalDateTime()))
                        Types.JZonedDateTimeAnyClass -> stmt.setTimestamp(pos, java.sql.Timestamp.valueOf(((arg as ZonedDateTime).toJava8ZonedDateTime()).toLocalDateTime()))
                        Types.JInstantAnyClass -> stmt.setTimestamp(pos, java.sql.Timestamp.valueOf((LocalDateTime.ofInstant(arg as Instant, ZoneId.systemDefault()).toJava8LocalDateTime())))
                        Types.JDateTimeAnyClass -> stmt.setTimestamp(pos, java.sql.Timestamp.valueOf(((arg as DateTime).local()).toJava8LocalDateTime()))
                    }
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getScalar(rs: ResultSet, typ: Class<*>): T? {
        val pos = 1

        return if (typ == Types.JStringClass) rs.getString(pos) as T
        else if (typ == Types.JBoolClass) rs.getBoolean(pos) as T
        else if (typ == Types.JShortClass) rs.getShort(pos) as T
        else if (typ == Types.JIntClass) rs.getInt(pos) as T
        else if (typ == Types.JLongClass) rs.getLong(pos) as T
        else if (typ == Types.JFloatClass) rs.getFloat(pos) as T
        else if (typ == Types.JDoubleClass) rs.getDouble(pos) as T
        // else if (typ == Types.JDecimalClass) rs.getBigDecimal(pos) as T
        else if (typ == Types.JLocalDateClass) rs.getDate(pos).toLocalDate() as T
        else if (typ == Types.JLocalTimeClass) rs.getTime(pos).toLocalTime() as T
        else if (typ == Types.JLocalDateTimeClass) rs.getTimestamp(pos).toLocalDateTime() as T
        else if (typ == Types.JZonedDateTimeClass) rs.getTimestamp(pos).toLocalDateTime() as T
        else if (typ == Types.JInstantClass) rs.getTimestamp(pos).toInstant() as T
        else if (typ == Types.JDateTimeClass) DateTimes.of(rs.getTimestamp(pos)) as T
        else null
    }

    fun getTypeFromLang(dataType: Class<*>): DataType =
            if (dataType == Types.JBoolClass) DataType.DTBool
            else if (dataType == Types.JStringClass) DataType.DTString
            else if (dataType == Types.JShortClass) DataType.DTShort
            else if (dataType == Types.JIntClass) DataType.DTNumber
            else if (dataType == Types.JLongClass) DataType.DTLong
            else if (dataType == Types.JFloatClass) DataType.DTFloat
            else if (dataType == Types.JDoubleClass) DataType.DTDouble
            // else if (dataType == Types.JDecimalClass) DataType.DbDecimal
            else if (dataType == Types.JLocalDateClass) DataType.DTLocalDate
            else if (dataType == Types.JLocalTimeClass) DataType.DTLocalTime
            else if (dataType == Types.JLocalDateTimeClass) DataType.DTLocalDateTime
            else if (dataType == Types.JZonedDateTimeClass) DataType.DTZonedDateTime
            else if (dataType == Types.JInstantClass) DataType.DTInstant
            else if (dataType == Types.JDateTimeClass) DataType.DTDateTime
            else DataType.DTString


    private fun LocalDate.toJava8LocalDate(): java.time.LocalDate {
        return java.time.LocalDate.of(this.year, this.month.value, this.dayOfMonth)
    }

    private fun LocalTime.toJava8LocalTime(): java.time.LocalTime {
        return java.time.LocalTime.of(this.hour, this.minute, this.second, this.nano)
    }

    private fun LocalDateTime.toJava8LocalDateTime(): java.time.LocalDateTime {
        return java.time.LocalDateTime.of(this.year, this.month.value, this.dayOfMonth,
            this.hour, this.minute, this.second, this.nano)
    }

    private fun ZonedDateTime.toJava8ZonedDateTime(): java.time.ZonedDateTime {
        return java.time.ZonedDateTime.of(this.year, this.month.value, this.dayOfMonth,
            this.hour, this.minute, this.second, this.nano, java.time.ZoneId.of(this.zone.id))
    }

    private fun Instant.toJava8Instant(): java.time.Instant {
        return java.time.Instant.ofEpochMilli(this.toEpochMilli())
    }
}
