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

package slatekit.common.query

import slatekit.common.DateTime


object QueryEncoder {


    fun convertVal(value: Any): String {
        return when (value) {
            Query.Null  -> "null"
            is String   -> toString(value)
            is Int      -> value.toString()
            is Long     -> value.toString()
            is Double   -> value.toString()
            is Boolean  -> if (value) "1" else "0"
            is DateTime -> "'" + value.toStringMySql() + "'"
            is List<*>  -> "(" + value.joinToString(",", transform = Any?::toString) + ")"
            is Array<*> -> "(" + value.joinToString(",", transform = Any?::toString) + ")"
            else        -> value.toString()
        }
    }


    /**
     * ensures the text value supplied be escaping single quotes for sql.
     *
     * @param text
     * @return
     */
    fun ensureValue(text: String): String =
            if (text.isNullOrEmpty()) {
                ""
            }
            else {
                text.replace("'", "''")
            }


    fun ensureField(text: String): String =
            if (text.isNullOrEmpty()) {
                ""
            }
            else {
                text.toLowerCase().trim().filter { c -> c.isDigit() || c.isLetter() || c == '_' || c == '.' }
            }


    /**
     * ensures the comparison operator to be any of ( = > >= < <= != is), other wise
     * defaults to "="
     *
     * @param compare
     * @return
     */
    fun ensureCompare(compare: String): String =
            when (compare) {
                "="      -> "="
                ">"      -> ">"
                ">="     -> ">="
                "<"      -> "<"
                "<="     -> "<="
                "!="     -> "<>"
                "is"     -> "is"
                "is not" -> "is not"
                "in"     -> "in"
                else     -> ensureField(compare)
            }


    fun toString(value: String): String {
        val s = QueryEncoder.ensureValue(value)
        val res = if (s.isNullOrEmpty()) "''" else "'$s'"
        return res
    }
}
