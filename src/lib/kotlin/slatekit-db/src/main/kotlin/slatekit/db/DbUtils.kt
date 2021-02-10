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
import slatekit.common.EnumLike
import slatekit.common.data.DataType
import slatekit.common.data.Value
import slatekit.common.ext.local
import slatekit.common.ids.ULID
import slatekit.common.ids.UPID
import java.util.*

object DbUtils {

    val jdbcTypes = mapOf<DataType, Int>(
        DataType.DTBool          to java.sql.Types.BOOLEAN,
        DataType.DTChar          to java.sql.Types.CHAR,
        DataType.DTString        to java.sql.Types.VARCHAR,
        DataType.DTText          to java.sql.Types.LONGNVARCHAR,
        DataType.DTShort         to java.sql.Types.SMALLINT,
        DataType.DTInt           to java.sql.Types.INTEGER,
        DataType.DTLong          to java.sql.Types.BIGINT,
        DataType.DTFloat         to java.sql.Types.FLOAT,
        DataType.DTDouble        to java.sql.Types.DOUBLE,
        DataType.DTDecimal       to java.sql.Types.DECIMAL,
        DataType.DTLocalDate     to java.sql.Types.DATE,
        DataType.DTLocalTime     to java.sql.Types.TIME,
        DataType.DTLocalDateTime to java.sql.Types.TIMESTAMP,
        DataType.DTZonedDateTime to java.sql.Types.TIMESTAMP,
        DataType.DTDateTime      to java.sql.Types.TIMESTAMP,
        DataType.DTInstant       to java.sql.Types.TIMESTAMP,
        DataType.DTEnum          to java.sql.Types.INTEGER,
        DataType.DTUUID          to java.sql.Types.VARCHAR,
        DataType.DTULID          to java.sql.Types.VARCHAR,
        DataType.DTUPID          to java.sql.Types.VARCHAR
    )

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
    fun fillArgs(stmt: PreparedStatement, inputs: List<Value>?, error: (Exception) -> Unit) {
        inputs?.forEachIndexed { index, arg ->
            val pos = index + 1
            try {
                when (arg.value) {
                    null -> {
                        val type = jdbcTypes[arg.tpe]
                        stmt.setNull(pos, type ?: java.sql.Types.INTEGER)
                    }
                    else -> {
                        when (arg.tpe) {
                            DataType.DTString -> stmt.setString(pos, arg.value.toString())
                            DataType.DTBool -> stmt.setBoolean(pos, arg.value as Boolean)
                            DataType.DTShort -> stmt.setShort(pos, arg.value as Short)
                            DataType.DTInt -> stmt.setInt(pos, arg.value as Int)
                            DataType.DTLong -> stmt.setLong(pos, arg.value as Long)
                            DataType.DTFloat -> stmt.setFloat(pos, arg.value as Float)
                            DataType.DTDouble -> stmt.setDouble(pos, arg.value as Double)
                            DataType.DTEnum -> stmt.setInt(pos, toEnumValue(arg.value!!))
                            DataType.DTLocalDate -> stmt.setDate(pos, java.sql.Date.valueOf((arg.value as LocalDate).toJava8LocalDate()))
                            DataType.DTLocalTime -> stmt.setTime(pos, java.sql.Time.valueOf((arg.value as LocalTime).toJava8LocalTime()))
                            DataType.DTLocalDateTime -> stmt.setTimestamp(pos, java.sql.Timestamp.valueOf((arg.value as LocalDateTime).toJava8LocalDateTime()))
                            DataType.DTZonedDateTime -> stmt.setTimestamp(pos, java.sql.Timestamp.valueOf(((arg.value as ZonedDateTime).toJava8ZonedDateTime()).toLocalDateTime()))
                            DataType.DTInstant -> stmt.setTimestamp(pos, java.sql.Timestamp.valueOf((LocalDateTime.ofInstant(arg.value as Instant, ZoneId.systemDefault()).toJava8LocalDateTime())))
                            DataType.DTDateTime -> stmt.setTimestamp(pos, java.sql.Timestamp.valueOf(((arg.value as DateTime).local()).toJava8LocalDateTime()))
                            DataType.DTUUID -> stmt.setString(pos, (arg.value as UUID).toString())
                            DataType.DTULID -> stmt.setString(pos, (arg.value as ULID).value)
                            DataType.DTUPID -> stmt.setString(pos, (arg.value as UPID).value)
                            else -> stmt.setString(pos, arg.value.toString())
                        }
                    }
                }
            }
            catch(ex:Exception) {
                error(ex)
            }
        }
    }

    fun toEnumValue(value:Any):Int {
        return when(value) {
            is Int      -> value
            is EnumLike -> value.value
            is Enum<*>  -> value.ordinal
            is String   -> value.toInt()
            else        -> value.toString().toInt()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getScalar(rs: ResultSet, typ: DataType): T? {
        val pos = 1

        return if (typ == DataType.DTString     ) rs.getString(pos) as T
        else if (typ == DataType.DTBool         ) rs.getBoolean(pos) as T
        else if (typ == DataType.DTShort        ) rs.getShort(pos) as T
        else if (typ == DataType.DTInt          ) rs.getInt(pos) as T
        else if (typ == DataType.DTLong         ) rs.getLong(pos) as T
        else if (typ == DataType.DTFloat        ) rs.getFloat(pos) as T
        else if (typ == DataType.DTDouble       ) rs.getDouble(pos) as T
        else if (typ == DataType.DTLocalDate    ) rs.getDate(pos).toLocalDate() as T
        else if (typ == DataType.DTLocalTime    ) rs.getTime(pos).toLocalTime() as T
        else if (typ == DataType.DTLocalDateTime) rs.getTimestamp(pos).toLocalDateTime() as T
        else if (typ == DataType.DTZonedDateTime) rs.getTimestamp(pos).toLocalDateTime() as T
        else if (typ == DataType.DTDateTime     ) DateTimes.of(rs.getTimestamp(pos)) as T
        else if (typ == DataType.DTInstant      ) rs.getTimestamp(pos).toInstant() as T
        else null
    }

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
