/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package kiit.query

import kiit.common.ext.toStringMySql
// import java.time.*
import org.threeten.bp.*
import kiit.common.ext.toNumeric
import java.util.*

object QueryEncoder {

    @JvmStatic
    fun convertVal(value: Any?): String {
        /* ktlint-disable */
        return when (value) {
            Const.Null       -> "null"
            null             -> "null"
            is String        -> toString(value)
            is Int           -> value.toString()
            is Long          -> value.toString()
            is Double        -> value.toString()
            is UUID          -> "'" + value.toString() + "'"
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

    /**
     * ensures the text value supplied be escaping single quotes for sql.
     *
     * @param text
     * @return
     */
    @JvmStatic
    fun ensureValue(text: String): String =
            if (text.isNullOrEmpty()) {
                ""
            } else {
                text.replace("'", "''")
            }

    @JvmStatic
    fun ensureField(text: String): String =
            if (text.isNullOrEmpty()) {
                ""
            } else {
                text.trim().filter { c -> c.isDigit() || c.isLetter() || c == '_' || c == '.' }
            }

    /**
     * ensures the comparison operator to be any of ( = > >= < <= != is), other wise
     * defaults to "="
     *
     * @param compare
     * @return
     */
    @JvmStatic
    fun ensureCompare(compare: String): String =
            when (compare) {
                "=" -> "="
                ">" -> ">"
                ">=" -> ">="
                "<" -> "<"
                "<=" -> "<="
                "!=" -> "<>"
                "is" -> "is"
                "is not" -> "is not"
                "in" -> "in"
                else -> ensureField(compare)
            }

    @JvmStatic
    fun toString(value: String): String {
        val s = ensureValue(value)
        val res = if (s.isNullOrEmpty()) "''" else "'$s'"
        return res
    }
}
