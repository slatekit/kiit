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
import kotlin.reflect.full.createType

object Types {

    val BoolClass   = Boolean::class
    val ShortClass  = Short::class
    val IntClass    = Int::class
    val LongClass   = Long::class
    val FloatClass  = Float::class
    val DoubleClass = Double::class
    val DateClass   = DateTime::class
    val StringClass = String::class


    val BoolType = Boolean::class.createType()
    val ShortType = Short::class.createType()
    val IntType = Int::class.createType()
    val LongType = Long::class.createType()
    val FloatType = Float::class.createType()
    val DoubleType = Double::class.createType()
    val DateType = DateTime::class.createType()
    val StringType = String::class.createType()


    fun getClassFromType(tpe: KType): KClass<*> {
        return when (tpe) {
            // Basic types
            Types.BoolType       -> Types.BoolClass
            Types.ShortType      -> Types.ShortClass
            Types.IntType        -> Types.IntClass
            Types.LongType       -> Types.LongClass
            Types.FloatType      -> Types.FloatClass
            Types.DoubleType     -> Types.DoubleClass
            Types.DateType       -> Types.DateClass
            Types.StringType     -> Types.StringClass
            else                 -> tpe.classifier as KClass<*>
        }
    }
}