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

package slatekit.common.convert

import slatekit.common.types.Doc
//import java.time.*
import org.threeten.bp.*
import slatekit.common.DateTime
import slatekit.common.DateTimes
import slatekit.common.Types
import slatekit.common.types.Vars
import slatekit.common.io.Uris

/**
 * Conversions from text to types
 * NOTE: While the standard library has extension methods
 * to convert string to a certain type, these are here to
 * support lamdas using in other places
 */
object Conversions {

    fun toString(text: String): String = text

    fun toBool(text: String): Boolean = text.toBoolean()

    fun toShort(text: String): Short = text.toShort()

    fun toInt(text: String): Int = text.toInt()

    fun toLong(text: String): Long = text.toLong()

    fun toFloat(text: String): Float = text.toFloat()

    fun toDouble(text: String): Double = text.toDouble()

    fun toInstant(text: String): Instant = Instant.parse(text)

    fun toLocalDate(text: String): LocalDate = LocalDate.parse(text)

    fun toLocalTime(text: String): LocalTime = LocalTime.parse(text)

    fun toLocalDateTime(text: String): LocalDateTime = LocalDateTime.parse(text)

    fun toZonedDateTime(text: String): ZonedDateTime = toDateTime(text)

    fun toZonedDateTimeUtc(text: String): ZonedDateTime = toZonedDateTime(text).withZoneSameLocal(ZoneId.of("UTC"))

    fun toDateTime(text: String): DateTime {
        return when (text) {
            "" -> DateTime.now()
            "@{today}" -> DateTimes.today()
            "@{tomorrow}" -> DateTimes.today().plusDays(1)
            "@{yesterday}" -> DateTimes.today().plusDays(-1)
            "@{now}" -> DateTime.now()
            else -> DateTimes.parse(text)
        }
    }

    /**
     * Builds a Vars object which is essentially a lookup of items by both index and key
     * @param args
     * @param paramName
     * @return
     */
    fun toVars(data: Any?): Vars {
        return when (data) {
            null -> Vars.of("")
            "null" -> Vars.of("")
            is String -> if (data.isNullOrEmpty()) Vars.of("") else Vars.of(data)
            else -> Vars.of("")
        }
    }

    /**
     * Builds a Doc object by reading the file content from the referenced uri
     * e.g.
     * 1. "user://slatekit/temp/file1.txt"    reference user directory
     * 2. "file://c:/slatekit/temp/file.txt"  reference file explicitly
     * @param args
     * @param paramName
     * @return
     */
    fun toDoc(uri: String): Doc {
        val doc = Uris.readDoc(uri)
        return doc ?: Doc.text("", "")
    }

    fun converterFor(tpe: Class<*>): (String) -> Any {
        val converter = when (tpe) {
        // Basic types
            Types.JBoolClass -> this::toBool
            Types.JShortClass -> this::toShort
            Types.JIntClass -> this::toInt
            Types.JLongClass -> this::toLong
            Types.JFloatClass -> this::toFloat
            Types.JDoubleClass -> this::toDouble
            Types.JLocalDateClass -> this::toLocalDate
            Types.JLocalTimeClass -> this::toLocalTime
            Types.JLocalDateTimeClass -> this::toLocalDateTime
            Types.JZonedDateTimeClass -> this::toZonedDateTime
            Types.JDateTimeClass -> this::toDateTime
            Types.JDocClass -> this::toDoc
            Types.JVarsClass -> this::toVars
            else -> this::toString
        }
        return converter
    }

    /**
     * Builds a string parameter ensuring that nulls are avoided.
     * @param args
     * @param paramName
     * @return
     */
    fun handleString(data: Any?): String {
        // As a design choice, this marshaller will only pass empty string to
        // API methods instead of null
        return when (data) {
            null -> ""
            "null" -> ""
            is String -> if (data.isNullOrEmpty()) "" else data
            else -> data.toString()
        }
    }
}
