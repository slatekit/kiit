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
    val onError: (Exception) -> Unit

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
    fun insert(sql: String, inputs: List<Any?>? = null): Long

    /**
     * executes an insert using the sql or stored proc and gets the id
     *
     * @param sql : The sql or stored proc
     * @param inputs : The inputs for the sql or stored proc
     * @return : The id ( primary key )
     */
    fun insertGetId(sql: String, inputs: List<Any?>? = null): String

    /**
     * executes the update sql or stored proc
     *
     * @param sql : The sql or stored proc
     * @param inputs : The inputs for the sql or stored proc
     * @return : The number of affected records
     */
    fun update(sql: String, inputs: List<Any?>? = null): Int

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
            inputs: List<Any?>? = null
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
    fun <T> mapOne(sql: String, inputs: List<Any?>?, mapper: (Record) -> T?): T?

    /**
     * maps multiple items using the sql supplied
     *
     * @param sql : The sql
     * @param mapper : THe mapper to map the item of type T
     * @tparam T     : The type of the item
     * @return
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> mapAll(sql: String, inputs: List<Any?>?, mapper: (Record) -> T?): List<T>?


    fun errorHandler(ex: Exception)

    /**
     * gets a scalar string value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun <T> getScalar(sql: String, typ: Class<*>, inputs: List<Any?>?): T =
            getScalarOrNull<T>(sql, typ, inputs)!!

    /**
     * gets a scalar string value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun <T> getScalarOrNull(sql: String, typ: Class<*>, inputs: List<Any?>?): T?


    /**
     * gets a scalar string value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarString(sql: String, inputs: List<Any?>?): String =
            getScalarOrNull<String>(sql, slatekit.common.Types.JStringClass, inputs) ?: ""

    /**
     * gets a scalar int value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarShort(sql: String, inputs: List<Any?>?): Short =
            getScalarOrNull(sql, slatekit.common.Types.JShortClass, inputs) ?: 0.toShort()

    /**
     * gets a scalar int value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarInt(sql: String, inputs: List<Any?>?): Int =
            getScalarOrNull(sql, slatekit.common.Types.JIntClass, inputs) ?: 0

    /**
     * gets a scalar long value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarLong(sql: String, inputs: List<Any?>?): Long =
            getScalarOrNull(sql, slatekit.common.Types.JLongClass, inputs) ?: 0L

    /**
     * gets a scalar double value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarFloat(sql: String, inputs: List<Any?>?): Float =
            getScalarOrNull(sql, slatekit.common.Types.JFloatClass, inputs) ?: 0.0f

    /**
     * gets a scalar double value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarDouble(sql: String, inputs: List<Any?>?): Double =
            getScalarOrNull(sql, slatekit.common.Types.JDoubleClass, inputs) ?: 0.0

    /**
     * gets a scalar bool value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarBool(sql: String, inputs: List<Any?>?): Boolean =
            getScalarOrNull(sql, slatekit.common.Types.JBoolClass, inputs) ?: false

    /**
     * gets a scalar local date value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarLocalDate(sql: String, inputs: List<Any?>?): LocalDate =
            getScalarOrNull(sql, slatekit.common.Types.JLocalDateClass, inputs) ?: LocalDate.MIN

    /**
     * gets a scalar local time value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarLocalTime(sql: String, inputs: List<Any?>?): LocalTime =
            getScalarOrNull(sql, slatekit.common.Types.JLocalTimeClass, inputs) ?: LocalTime.MIN

    /**
     * gets a scalar local datetime value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarLocalDateTime(sql: String, inputs: List<Any?>?): LocalDateTime =
            getScalarOrNull(sql, slatekit.common.Types.JLocalDateTimeClass, inputs) ?: LocalDateTime.MIN

    /**
     * gets a scalar local datetime value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarZonedDateTime(sql: String, inputs: List<Any?>?): DateTime =
            getScalarOrNull(sql, slatekit.common.Types.JDateTimeClass, inputs) ?: DateTimes.MIN
}