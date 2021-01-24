package slatekit.common.data

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZonedDateTime
import slatekit.common.ext.toNumeric
import slatekit.common.ext.toStringMySql
import java.util.*

object Encoding {

    @JvmStatic
    fun convertVal(value: Any?): String {
        /* ktlint-disable */
        return when (value) {
            null             -> "null"
            is String        -> toString(value)
            is Int           -> value.toString()
            is Long          -> value.toString()
            is Double        -> value.toString()
            is UUID          -> "'$value'"
            is Boolean       -> if (value) "1" else "0"
            is LocalDate     -> value.toNumeric().toString()
            is LocalTime     -> value.toNumeric().toString()
            is LocalDateTime -> "'" + value.toStringMySql() + "'"
            is ZonedDateTime -> "'" + value.toStringMySql() + "'"
            is List<*>       -> "(" + value.joinToString(",", transform = { it -> convertVal(it) }) + ")"
            is Array<*>      -> "(" + value.joinToString(",", transform = { it -> convertVal(it) }) + ")"
            is Enum<*>       -> value.ordinal.toString()
            else             -> value.toString()
        }
        /* ktlint-enable */
    }

    @JvmStatic
    fun toString(value: String): String {
        val s = ensureValue(value)
        return if (s.isNullOrEmpty()) "''" else "'$s'"
    }

    /**
     * ensures the text value supplied be escaping single quotes for sql.
     *
     * @param text
     * @return
     */
    @JvmStatic
    fun ensureValue(text: String): String {
        return when(text.isNullOrEmpty()) {
            true -> ""
            false -> text.replace("'", "''")
        }
    }

    @JvmStatic
    fun ensureField(text: String): String {
        return when(text.isNullOrEmpty()) {
            true -> ""
            false -> text.toLowerCase().trim().filter { c -> c.isDigit() || c.isLetter() || c == '_' || c == '.' }
        }
    }
}