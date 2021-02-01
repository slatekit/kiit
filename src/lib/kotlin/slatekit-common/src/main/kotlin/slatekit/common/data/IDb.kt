package slatekit.common.data

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import slatekit.common.DateTime
import slatekit.common.DateTimes
import slatekit.common.Record
import java.sql.ResultSet

/**
 * Interface to abstract away JDBC.
 * This is used for 2 purposes:
 * 1. Facilitate Unit Testing
 * 2. Facilitate support for the Entities / ORM ( SqlFramework ) project
 *    to abstract away JDBC for Android
 */
interface IDb : ProcSupport {
    val errHandler: (Exception) -> Unit

    /**
     * registers the jdbc driver
     *
     * @return
     */
    fun open(): IDb

    /**
     * Executes the sql provided
     */
    fun execute(sql: String)

    /**
     * executes an insert using the sql or stored proc and gets the id
     *
     * @param sql : The sql or stored proc
     * @param inputs : The inputs for the sql or stored proc
     * @return : The id ( primary key )
     */
    fun insert(sql: String, inputs: List<Value>? = null): Long

    /**
     * executes an insert using the sql or stored proc and gets the id
     *
     * @param sql : The sql or stored proc
     * @param inputs : The inputs for the sql or stored proc
     * @return : The id ( primary key )
     */
    fun insertGetId(sql: String, inputs: List<Value>? = null): String

    /**
     * executes the update sql or stored proc
     *
     * @param sql : The sql or stored proc
     * @param inputs : The inputs for the sql or stored proc
     * @return : The number of affected records
     */
    fun update(sql: String, inputs: List<Value>? = null): Int

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
            inputs: List<Value>? = null
    ): T?

    /**
     * maps a single item using the sql supplied
     *
     * @param sql : The sql
     * @param mapper : THe mapper to map the item of type T
     * @tparam T     : The type of the item
     * @return
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> mapOne(sql: String, inputs: List<Value>?, mapper: (Record) -> T?): T?

    /**
     * maps multiple items using the sql supplied
     *
     * @param sql : The sql
     * @param mapper : THe mapper to map the item of type T
     * @tparam T     : The type of the item
     * @return
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> mapAll(sql: String, inputs: List<Value>?, mapper: (Record) -> T?): List<T>?


    fun errorHandler(ex: Exception)

    /**
     * gets a scalar string value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun <T> getScalar(sql: String, typ: DataType, inputs: List<Value>?): T =
            getScalarOrNull<T>(sql, typ, inputs)!!

    /**
     * gets a scalar string value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun <T> getScalarOrNull(sql: String, typ: DataType, inputs: List<Value>?): T?

    /**
     * gets a scalar bool value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarBool(sql: String, inputs: List<Value>?): Boolean =
            getScalarOrNull(sql, DataType.DTBool, inputs) ?: false


    /**
     * gets a scalar string value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarString(sql: String, inputs: List<Value>?): String =
            getScalarOrNull<String>(sql, DataType.DTString, inputs) ?: ""

    /**
     * gets a scalar int value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarShort(sql: String, inputs: List<Value>?): Short =
            getScalarOrNull(sql, DataType.DTShort, inputs) ?: 0.toShort()

    /**
     * gets a scalar int value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarInt(sql: String, inputs: List<Value>?): Int =
            getScalarOrNull(sql, DataType.DTInt, inputs) ?: 0

    /**
     * gets a scalar long value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarLong(sql: String, inputs: List<Value>?): Long =
            getScalarOrNull(sql, DataType.DTLong, inputs) ?: 0L

    /**
     * gets a scalar double value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarFloat(sql: String, inputs: List<Value>?): Float =
            getScalarOrNull(sql, DataType.DTFloat, inputs) ?: 0.0f

    /**
     * gets a scalar double value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarDouble(sql: String, inputs: List<Value>?): Double =
            getScalarOrNull(sql, DataType.DTDouble, inputs) ?: 0.0

    /**
     * gets a scalar local date value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarLocalDate(sql: String, inputs: List<Value>?): LocalDate =
            getScalarOrNull(sql, DataType.DTLocalDate, inputs) ?: LocalDate.MIN

    /**
     * gets a scalar local time value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarLocalTime(sql: String, inputs: List<Value>?): LocalTime =
            getScalarOrNull(sql, DataType.DTLocalTime, inputs) ?: LocalTime.MIN

    /**
     * gets a scalar local datetime value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarLocalDateTime(sql: String, inputs: List<Value>?): LocalDateTime =
            getScalarOrNull(sql, DataType.DTLocalDateTime, inputs) ?: LocalDateTime.MIN

    /**
     * gets a scalar local datetime value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarZonedDateTime(sql: String, inputs: List<Value>?): DateTime =
            getScalarOrNull(sql, DataType.DTZonedDateTime, inputs) ?: DateTimes.MIN
}