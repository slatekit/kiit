package slatekit.common.db

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import slatekit.common.DateTime
import slatekit.common.DateTimes

interface ScalarSupport {

    /**
     * gets a scalar string value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun <T> getScalar(sql: String, typ: Class<*>, inputs: List<Any>?): T =
            getScalarOrNull<T>(sql, typ, inputs)!!

    /**
     * gets a scalar string value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun <T> getScalarOrNull(sql: String, typ: Class<*>, inputs: List<Any>?): T?


    /**
     * gets a scalar string value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarString(sql: String, inputs: List<Any>?): String =
            getScalarOrNull<String>(sql, slatekit.common.Types.JStringClass, inputs) ?: ""

    /**
     * gets a scalar int value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarShort(sql: String, inputs: List<Any>?): Short =
            getScalarOrNull(sql, slatekit.common.Types.JShortClass, inputs) ?: 0.toShort()

    /**
     * gets a scalar int value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarInt(sql: String, inputs: List<Any>?): Int =
            getScalarOrNull(sql, slatekit.common.Types.JIntClass, inputs) ?: 0

    /**
     * gets a scalar long value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarLong(sql: String, inputs: List<Any>?): Long =
            getScalarOrNull(sql, slatekit.common.Types.JLongClass, inputs) ?: 0L

    /**
     * gets a scalar double value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarFloat(sql: String, inputs: List<Any>?): Float =
            getScalarOrNull(sql, slatekit.common.Types.JFloatClass, inputs) ?: 0.0f

    /**
     * gets a scalar double value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarDouble(sql: String, inputs: List<Any>?): Double =
            getScalarOrNull(sql, slatekit.common.Types.JDoubleClass, inputs) ?: 0.0

    /**
     * gets a scalar bool value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarBool(sql: String, inputs: List<Any>?): Boolean =
            getScalarOrNull(sql, slatekit.common.Types.JBoolClass, inputs) ?: false

    /**
     * gets a scalar local date value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarLocalDate(sql: String, inputs: List<Any>?): LocalDate =
            getScalarOrNull(sql, slatekit.common.Types.JLocalDateClass, inputs) ?: LocalDate.MIN

    /**
     * gets a scalar local time value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarLocalTime(sql: String, inputs: List<Any>?): LocalTime =
            getScalarOrNull(sql, slatekit.common.Types.JLocalTimeClass, inputs) ?: LocalTime.MIN

    /**
     * gets a scalar local datetime value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarLocalDateTime(sql: String, inputs: List<Any>?): LocalDateTime =
            getScalarOrNull(sql, slatekit.common.Types.JLocalDateTimeClass, inputs) ?: LocalDateTime.MIN

    /**
     * gets a scalar local datetime value using the sql provided
     *
     * @param sql : The sql text
     * @return
     */
    fun getScalarDate(sql: String, inputs: List<Any>?): DateTime =
            getScalarOrNull(sql, slatekit.common.Types.JDateTimeClass, inputs) ?: DateTimes.MIN
}