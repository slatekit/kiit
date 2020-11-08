package slatekit.apis.tools.code

import slatekit.context.Context
import slatekit.common.requests.Request
import slatekit.meta.KTypes
import kotlin.reflect.KClass
import kotlin.reflect.KType

/** Represents type information for the purspose of code generation.
 * @param isBasicType : whether this is a number, boolean, String
 * @param isCollection : whether this is a list or map
 * @param targetType : The target type name used as a parameter input: e.g. "int", "double"
 * @param targetTypeName : The target type name used as a return type. For parsing/generic purposes,
 *                               this is the corresponding object type for a type. e.g. Integer for int.
 *                               e.g. "Integer", "Double"
 * @param containerType : For collections, the Kotlin class representing the container type. e.g. "List::class", "Map::class"
 * @param dataType : The Kotlin class representing the data type.
 * @param conversionType : The Kotlin
 */
data class TypeInfo(
    val isBasicType: Boolean,
    val isCollection: Boolean,
    val targetTypes: List<KClass<*>>,
    val containerType: KClass<*>? = null,
    val targetTypeNameBuilder:((KClass<*>) -> String)? = null
) {

    /**
     * First type e.g. For most cases
     */
    val targetType:KClass<*> = targetTypes.first()


    /**
     * Converts the target Type name to actual type name
     */
    val converter:(KClass<*>) -> String = { cls -> targetTypeNameBuilder?.let { it(cls) } ?: cls.simpleName!! }

    /**
     * For basic types:
     * 1. Basic types: String, Int, Boolean
     * 2. Containers : List<String>, Map<String, String>
     * 3. Objects    : User
     *
     */
    val targetTypeName: String = converter.invoke(targetType)

    /**
     * Generates comma delimited lists of the types e.g. String, Int
     * Used for List, Map to create type signature as List<String> or Map<String, Int>
     */
    val parameterizedNames:String = targetTypes.joinToString { ttype -> converter.invoke(ttype) }


    fun returnType():String {
        return when {
            isList() -> "List<$parameterizedNames>"
            isMap()  -> "Map<$parameterizedNames>"
            isPair() -> "Pair<$parameterizedNames>"
            else     -> parameterizedNames
        }
    }


    /**
     * Generates comma delimited lists of the types e.g. String, Int
     * Used for List, Map to create type signature as List<String> or Map<String, Int>
     */
    fun parameterizedTypes(suffix:String):String = targetTypes.joinToString { converter.invoke(it) + suffix }


    fun isList(): Boolean = containerType == List::class
    fun isMap(): Boolean = containerType == Map::class
    fun isObject(): Boolean = !isBasicType && !isCollection
    fun isPair(): Boolean = isObject() && targetType.simpleName?.startsWith("Pair") ?: false


    fun isApplicableForCodeGen(): Boolean {
        return !this.isBasicType &&
            !this.isCollection &&
            this.targetType != Request::class &&
            this.targetType != Any::class &&
            this.targetType != Context::class
    }


    fun converterTypeName(): String {
        return when {
            isList() -> List::class.simpleName!!
            isMap()  -> Map::class.simpleName!!
            isPair() -> Pair::class.simpleName!!
            else     -> "Single"
        }
    }


    companion object {
        val basicTypes:Map<KType, TypeInfo> = mapOf(
            // Basic types
            KTypes.KStringType        to TypeInfo(true, false, listOf(KTypes.KStringClass       ), null),
            KTypes.KBoolType          to TypeInfo(true, false, listOf(KTypes.KBoolClass         ), null),
            KTypes.KShortType         to TypeInfo(true, false, listOf(KTypes.KShortClass        ), null),
            KTypes.KIntType           to TypeInfo(true, false, listOf(KTypes.KIntClass          ), null),
            KTypes.KLongType          to TypeInfo(true, false, listOf(KTypes.KLongClass         ), null),
            KTypes.KFloatType         to TypeInfo(true, false, listOf(KTypes.KFloatClass        ), null),
            KTypes.KDoubleType        to TypeInfo(true, false, listOf(KTypes.KDoubleClass       ), null),
            KTypes.KDateTimeType      to TypeInfo(true, false, listOf(KTypes.KDateTimeClass     ), null),
            KTypes.KLocalDateType     to TypeInfo(true, false, listOf(KTypes.KLocalDateClass    ), null),
            KTypes.KLocalTimeType     to TypeInfo(true, false, listOf(KTypes.KLocalTimeClass    ), null),
            KTypes.KLocalDateTimeType to TypeInfo(true, false, listOf(KTypes.KLocalDateTimeClass), null),
            KTypes.KZonedDateTimeType to TypeInfo(true, false, listOf(KTypes.KZonedDateTimeClass), null),
            KTypes.KDocType           to TypeInfo(true, false, listOf(KTypes.KDocClass          ), null),
            KTypes.KVarsType          to TypeInfo(true, false, listOf(KTypes.KVarsClass         ), null),
            KTypes.KSmartValueType    to TypeInfo(true, false, listOf(KTypes.KSmartValueClass   ), null),
            KTypes.KUPIDType      to TypeInfo(true, false, listOf(KTypes.KUPIDClass     ), null),
            KTypes.KUUIDType          to TypeInfo(true, false, listOf(KTypes.KUUIDClass         ), null),
            KTypes.KContentType       to TypeInfo(true, false, listOf(KTypes.KContentClass      ), null),
            KTypes.KDecStringType     to TypeInfo(true, false, listOf(KTypes.KDecStringClass    ), null),
            KTypes.KDecIntType        to TypeInfo(true, false, listOf(KTypes.KDecIntClass       ), null),
            KTypes.KDecLongType       to TypeInfo(true, false, listOf(KTypes.KDecLongClass      ), null),
            KTypes.KDecDoubleType     to TypeInfo(true, false, listOf(KTypes.KDecDoubleClass    ), null),
            KTypes.KAnyType           to TypeInfo(false,false, listOf(KTypes.KAnyClass          ), null)
        )
    }
}
