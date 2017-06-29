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

package slatekit.common

import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Created by kishorereddy on 6/9/17.
 */
object Converter {

    fun convertToString(text: String): String = text

    fun convertToBool(text: String): Boolean = text.toBoolean()

    fun convertToShort(text: String): Short = text.toShort()

    fun convertToInt(text: String): Int = text.toInt()

    fun convertToLong(text: String): Long = text.toLong()

    fun convertToFloat(text: String): Float = text.toFloat()

    fun convertToDouble(text: String): Double = text.toDouble()

    fun convertToDate(text: String): DateTime = InputFuncs.convertDate(text)


    fun converterFor(tpe: KClass<*>): (String) -> Any {
        val converter = when (tpe) {
        // Basic types
            Types.BoolClass   -> { s: String -> convertToBool(s) }
            Types.ShortClass  -> { s: String -> convertToShort(s) }
            Types.IntClass    -> { s: String -> convertToInt(s) }
            Types.LongClass   -> { s: String -> convertToLong(s) }
            Types.FloatClass  -> { s: String -> convertToFloat(s) }
            Types.DoubleClass -> { s: String -> convertToDouble(s) }
            Types.DateClass   -> { s: String -> convertToDate(s) }
            Types.StringClass -> { s: String -> convertToString(s) }
            else              -> { s: String -> convertToString(s) }
        }
        return converter
    }


    fun converterFor(tpe: KType): (String) -> Any {
        val converter = when (tpe) {
        // Basic types
            Types.BoolType   -> { s: String -> convertToBool(s) }
            Types.ShortType  -> { s: String -> convertToShort(s) }
            Types.IntType    -> { s: String -> convertToInt(s) }
            Types.LongType   -> { s: String -> convertToLong(s) }
            Types.FloatType  -> { s: String -> convertToFloat(s) }
            Types.DoubleType -> { s: String -> convertToDouble(s) }
            Types.DateType   -> { s: String -> convertToDate(s) }
            Types.StringType -> { s: String -> convertToString(s) }
            else             -> { s: String -> convertToString(s) }
        }
        return converter
    }
}