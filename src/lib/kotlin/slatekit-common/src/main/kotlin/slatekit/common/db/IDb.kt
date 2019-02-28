package slatekit.common.db

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import slatekit.common.DateTime
import slatekit.common.DateTimes
import java.sql.ResultSet

interface IDb {
    val onError: (Exception) -> Unit

    /**
     * registers the jdbc driver
     *
     * @return
     */
    fun open(): IDb

    fun execute(sql: String)



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
     fun getScalarString(sql: String, inputs: List<Any>?): String =
            getScalar<String>(sql, slatekit.common.Types.JStringClass, inputs) ?: ""

    /**
     * gets a scalar int value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
     fun getScalarShort(sql: String, inputs: List<Any>?): Short =
            getScalar(sql, slatekit.common.Types.JShortClass, inputs) ?: 0.toShort()

    /**
     * gets a scalar int value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
     fun getScalarInt(sql: String, inputs: List<Any>?): Int =
            getScalar(sql, slatekit.common.Types.JIntClass, inputs) ?: 0

    /**
     * gets a scalar long value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
     fun getScalarLong(sql: String, inputs: List<Any>?): Long =
            getScalar(sql, slatekit.common.Types.JLongClass, inputs) ?: 0L

    /**
     * gets a scalar double value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
     fun getScalarFloat(sql: String, inputs: List<Any>?): Float =
            getScalar(sql, slatekit.common.Types.JFloatClass, inputs) ?: 0.0f

    /**
     * gets a scalar double value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
     fun getScalarDouble(sql: String, inputs: List<Any>?): Double =
            getScalar(sql, slatekit.common.Types.JDoubleClass, inputs) ?: 0.0

    /**
     * gets a scalar bool value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
     fun getScalarBool(sql: String, inputs: List<Any>?): Boolean =
            getScalar(sql, slatekit.common.Types.JBoolClass, inputs) ?: false

    /**
     * gets a scalar local date value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
     fun getScalarLocalDate(sql: String, inputs: List<Any>?): LocalDate =
            getScalar(sql, slatekit.common.Types.JLocalDateClass, inputs) ?: LocalDate.MIN

    /**
     * gets a scalar local time value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
     fun getScalarLocalTime(sql: String, inputs: List<Any>?): LocalTime =
            getScalar(sql, slatekit.common.Types.JLocalTimeClass, inputs) ?: LocalTime.MIN

    /**
     * gets a scalar local datetime value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
     fun getScalarLocalDateTime(sql: String, inputs: List<Any>?): LocalDateTime =
            getScalar(sql, slatekit.common.Types.JLocalDateTimeClass, inputs) ?: LocalDateTime.MIN

    /**
     * gets a scalar local datetime value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
     fun getScalarDate(sql: String, inputs: List<Any>?): DateTime =
            getScalar(sql, slatekit.common.Types.JDateTimeClass, inputs) ?: DateTimes.MIN

    /**
     * gets a scalar string value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun <T> getScalarOpt(sql: String, typ: Class<*>, inputs: List<Any>?): T?

    /**
     * executes an insert using the sql or stored proc and gets the id
     *
     * @param sql : The sql or stored proc
     * @param inputs : The inputs for the sql or stored proc
     * @return : The id ( primary key )
     */
    fun insert(sql: String, inputs: List<Any>? = null): Long

    /**
     * executes an insert using the sql or stored proc and gets the id
     *
     * @param sql : The sql or stored proc
     * @param inputs : The inputs for the sql or stored proc
     * @return : The id ( primary key )
     */
    fun insertAndGetStringId(sql: String, inputs: List<Any>? = null): String

    /**
     * executes the update sql or stored proc
     *
     * @param sql : The sql or stored proc
     * @param inputs : The inputs for the sql or stored proc
     * @return : The number of affected records
     */
    fun update(sql: String, inputs: List<Any>? = null): Int

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
    fun <T> mapOne(sql: String, mapper: Mapper, inputs: List<Any>? = null): T?

    /**
     * maps multiple items using the sql supplied
     *
     * @param sql : The sql
     * @param mapper : THe mapper to map the item of type T
     * @tparam T     : The type of the item
     * @return
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> mapMany(sql: String, mapper: Mapper, inputs: List<Any>? = null): List<T>?

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
    ): T?

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
    ): List<T>?

    /**
     * Calls a stored procedure
     * @param procName : The name of the stored procedure e.g. get_by_id
     * @param callback : The callback to handle the resultset
     * @param moveNext : Whether or not to automatically move the resultset to the next/first row
     * @param inputs : The parameters for the stored proc. The types will be auto-converted my-sql types.
     */
    fun callUpdate(procName: String, inputs: List<Any>? = null): Int


    fun errorHandler(ex: Exception)
}