package slatekit.apis.tools.code.builders

import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import slatekit.apis.Verb
import slatekit.apis.core.Action
import slatekit.apis.tools.code.CodeGenSettings
import slatekit.apis.tools.code.TypeInfo
import slatekit.common.newline
import slatekit.meta.KTypes
import slatekit.meta.Reflector

class KotlinBuilder(val settings: CodeGenSettings) : CodeBuilder {

    override val basicTypes = listOf(
            // Basic types
            Pair(KTypes.KStringType, TypeInfo(true, false, "String", "String", KTypes.KStringClass, KTypes.KStringClass, "String")),
            Pair(KTypes.KBoolType, TypeInfo(true, false, "Boolean", "Boolean", KTypes.KBoolClass, KTypes.KBoolClass, "Boolean")),
            Pair(KTypes.KShortType, TypeInfo(true, false, "Short", "Short", KTypes.KShortClass, KTypes.KShortClass, "Short")),
            Pair(KTypes.KIntType, TypeInfo(true, false, "Int", "Int", KTypes.KIntClass, KTypes.KIntClass, "Int")),
            Pair(KTypes.KLongType, TypeInfo(true, false, "Long", "Long", KTypes.KLongClass, KTypes.KLongClass, "Long")),
            Pair(KTypes.KFloatType, TypeInfo(true, false, "Float", "Float", KTypes.KFloatClass, KTypes.KFloatClass, "Float")),
            Pair(KTypes.KDoubleType, TypeInfo(true, false, "Double", "Double", KTypes.KDoubleClass, KTypes.KDoubleClass, "Double")),
            Pair(KTypes.KDateTimeType, TypeInfo(true, false, "Date", "Date", KTypes.KDateTimeClass, KTypes.KDateTimeClass, "DateTime")),
            Pair(KTypes.KLocalDateType, TypeInfo(true, false, "Date", "Date", KTypes.KLocalDateClass, KTypes.KLocalDateClass, "LocalDate")),
            Pair(KTypes.KLocalTimeType, TypeInfo(true, false, "Date", "Date", KTypes.KLocalTimeClass, KTypes.KLocalTimeClass, "LocalTime")),
            Pair(KTypes.KLocalDateTimeType, TypeInfo(true, false, "Date", "Date", KTypes.KLocalDateTimeClass, KTypes.KLocalDateTimeClass, "LocalDateTime")),
            Pair(KTypes.KZonedDateTimeType, TypeInfo(true, false, "Date", "Date", KTypes.KZonedDateTimeClass, KTypes.KZonedDateTimeClass, "ZonedDateTime")),
            Pair(KTypes.KDocType, TypeInfo(true, false, "String", "String", KTypes.KDocClass, KTypes.KDocClass, "String")),
            Pair(KTypes.KVarsType, TypeInfo(true, false, "String", "String", KTypes.KVarsClass, KTypes.KVarsClass, "String")),
            Pair(KTypes.KSmartValueType, TypeInfo(true, false, "String", "String", KTypes.KSmartValueClass, KTypes.KSmartValueClass, "String")),
            Pair(KTypes.KUniqueIdType, TypeInfo(true, false, "String", "String", KTypes.KUniqueIdClass, KTypes.KUniqueIdClass, "String")),
            Pair(KTypes.KUUIDType, TypeInfo(true, false, "String", "String", KTypes.KUUIDClass, KTypes.KUUIDClass, "String")),
            Pair(KTypes.KContentType, TypeInfo(true, false, "String", "String", KTypes.KContentClass, KTypes.KContentClass, "String")),
            Pair(KTypes.KDecStringType, TypeInfo(true, false, "String", "String", KTypes.KDecStringClass, KTypes.KDecStringClass, "String")),
            Pair(KTypes.KDecIntType, TypeInfo(true, false, "String", "String", KTypes.KDecIntClass, KTypes.KDecIntClass, "String")),
            Pair(KTypes.KDecLongType, TypeInfo(true, false, "String", "String", KTypes.KDecLongClass, KTypes.KDecLongClass, "String")),
            Pair(KTypes.KDecDoubleType, TypeInfo(true, false, "String", "String", KTypes.KDecDoubleClass, KTypes.KDecDoubleClass, "String")),
            Pair(KTypes.KAnyType, TypeInfo(false, false, "Object", "Object", KTypes.KAnyClass, KTypes.KAnyClass, "Any"))
    ).toMap()

    override fun buildModelInfo(cls: KClass<*>): String {

        val props = Reflector.getProperties(cls)
        val fields = props.foldIndexed("") { ndx: Int, acc: String, prop: KProperty<*> ->
            val type = prop.returnType
            val typeInfo = buildTypeName(type)
            val suffix = if (ndx < props.size - 1) "," else ""
            val field = "val " + prop.name + " : " + typeInfo.targetParameterType + suffix + newline
            acc + (if (ndx > 0) "\t" else "") + field
        }
        return fields
    }

    override fun buildArg(parameter: KParameter): String {
        return parameter.name + " : " + buildTypeName(parameter.type).targetParameterType
    }

    /**
     * builds a string of parameters to put into the query string.
     * e.g. queryParams.put("id", id);
     */
    override fun buildQueryParams(reg: Action): String {
        return if (reg.verb == Verb.Get) {
            reg.paramsUser.foldIndexed("") { ndx: Int, acc: String, param: KParameter ->
                acc + (if (ndx > 0) "\t\t" else "") + "queryParams.put(\"" + param.name + "\", " + param.name + ".toString());" + newline
            }
        } else {
            ""
        }
    }

    /**
     * builds a string of the parameters to put into the entity/body of request
     * e..g dataParams.put('id", id);
     */
    override fun buildDataParams(reg: Action): String {
        return if (reg.verb != Verb.Get) {
            reg.paramsUser.foldIndexed("") { ndx: Int, acc: String, param: KParameter ->
                acc + (if (ndx > 0) "\t\t" else "") + "postData.put(\"" + param.name + "\", " + param.name + ");" + newline
            }
        } else {
            ""
        }
    }
}
