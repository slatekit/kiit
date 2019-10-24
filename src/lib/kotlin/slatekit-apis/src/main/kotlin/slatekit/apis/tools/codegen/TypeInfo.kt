package slatekit.apis.tools.codegen

import kotlin.reflect.KClass

/** Represents type information for the purspose of code generation.
 * @param isBasicType : whether this is a number, boolean, String
 * @param isCollection : whether this is a list or map
 * @param targetParameterType : The target type name used as a parameter input: e.g. "int", "double"
 * @param targetReturnType : The target type name used as a return type. For parsing/generic purposes,
 *                               this is the corresponding object type for a type. e.g. Integer for int.
 *                               e.g. "Integer", "Double"
 * @param containerType : For collections, the Kotlin class representing the container type. e.g. "List::class", "Map::class"
 * @param dataType : The Kotlin class representing the data type.
 * @param conversionType : The Kotlin
 */
data class TypeInfo(
        val isBasicType: Boolean,
        val isCollection: Boolean,
        val targetParameterType: String,
        val targetReturnType: String,
        val containerType: KClass<*>,
        val dataType: KClass<*>,
        val conversionType: String,
        val keyType: KClass<*>? = null
) {
    fun isList()  : Boolean = containerType == List::class
    fun isMap()   : Boolean = containerType == Map::class
    fun isObject(): Boolean = !isBasicType && !isCollection
    fun isPair(): Boolean = isObject() && dataType.simpleName?.startsWith("Pair") ?: false
}