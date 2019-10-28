package slatekit.apis.tools.code

import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import slatekit.apis.Verb
import slatekit.apis.core.Action
import slatekit.common.newline
import slatekit.meta.KTypes
import slatekit.meta.Reflector

class CodeGenSwift(settings: CodeGenSettings) : CodeGenBase(settings) {

    override val basicTypes = listOf(
            // Basic types
            Pair(KTypes.KStringType, TypeInfo(true, false, "String", "String", KTypes.KStringClass, KTypes.KStringClass, "String" + ".class")),
            Pair(KTypes.KBoolType, TypeInfo(true, false, "Bool", "Bool", KTypes.KBoolClass, KTypes.KBoolClass, "Boolean" + ".class")),
            Pair(KTypes.KShortType, TypeInfo(true, false, "Short", "Short", KTypes.KShortClass, KTypes.KShortClass, "Short" + ".class")),
            Pair(KTypes.KIntType, TypeInfo(true, false, "Int", "Int", KTypes.KIntClass, KTypes.KIntClass, "Integer" + ".class")),
            Pair(KTypes.KLongType, TypeInfo(true, false, "Long", "Long", KTypes.KLongClass, KTypes.KLongClass, "Long" + ".class")),
            Pair(KTypes.KFloatType, TypeInfo(true, false, "Float", "Float", KTypes.KFloatClass, KTypes.KFloatClass, "Float" + ".class")),
            Pair(KTypes.KDoubleType, TypeInfo(true, false, "Double", "Double", KTypes.KDoubleClass, KTypes.KDoubleClass, "Double" + ".class")),
            Pair(KTypes.KDateTimeType, TypeInfo(true, false, "DateTime", "DateTime", KTypes.KDateTimeClass, KTypes.KDateTimeClass, "Date" + ".class")),
            Pair(KTypes.KLocalDateType, TypeInfo(true, false, "DateTime", "DateTime", KTypes.KLocalDateClass, KTypes.KLocalDateClass, "Date" + ".class")),
            Pair(KTypes.KLocalTimeType, TypeInfo(true, false, "DateTime", "DateTime", KTypes.KLocalTimeClass, KTypes.KLocalTimeClass, "Date" + ".class")),
            Pair(KTypes.KLocalDateTimeType, TypeInfo(true, false, "DateTime", "DateTime", KTypes.KLocalDateTimeClass, KTypes.KLocalDateTimeClass, "Date" + ".class")),
            Pair(KTypes.KZonedDateTimeType, TypeInfo(true, false, "DateTime", "DateTime", KTypes.KZonedDateTimeClass, KTypes.KZonedDateTimeClass, "Date" + ".class")),
            Pair(KTypes.KDocType, TypeInfo(true, false, "String", "String", KTypes.KDocClass, KTypes.KDocClass, "String" + ".class")),
            Pair(KTypes.KVarsType, TypeInfo(true, false, "String", "String", KTypes.KVarsClass, KTypes.KVarsClass, "String" + ".class")),
            Pair(KTypes.KSmartValueType, TypeInfo(true, false, "String", "String", KTypes.KSmartValueClass, KTypes.KSmartValueClass, "String" + ".class")),
            Pair(KTypes.KContentType, TypeInfo(true, false, "String", "String", KTypes.KContentClass, KTypes.KContentClass, "String" + ".class")),
            Pair(KTypes.KDecStringType, TypeInfo(true, false, "String", "String", KTypes.KDecStringClass, KTypes.KDecStringClass, "String" + ".class")),
            Pair(KTypes.KDecIntType, TypeInfo(true, false, "String", "String", KTypes.KDecIntClass, KTypes.KDecIntClass, "String" + ".class")),
            Pair(KTypes.KDecLongType, TypeInfo(true, false, "String", "String", KTypes.KDecLongClass, KTypes.KDecLongClass, "String" + ".class")),
            Pair(KTypes.KDecDoubleType, TypeInfo(true, false, "String", "String", KTypes.KDecDoubleClass, KTypes.KDecDoubleClass, "String" + ".class")),
            Pair(KTypes.KAnyType, TypeInfo(false, false, "Object", "Object", KTypes.KAnyClass, KTypes.KAnyClass, "Object" + ".class"))
    ).toMap()

    override fun buildModelInfo(cls: KClass<*>): String {
        val props = Reflector.getProperties(cls)
        val fields = props.foldIndexed("", { ndx: Int, acc: String, prop: KProperty<*> ->
            val type = prop.returnType
            val typeInfo = buildTypeName(type)
            val suffix = if (ndx < props.size - 1) "," else ""
            val field = "val " + prop.name + " : " + typeInfo.targetParameterType + suffix + newline
            acc + (if (ndx > 0) "\t" else "") + field
        })
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
        return if (reg.verb == Verb.Read) {
            reg.paramsUser.foldIndexed("", { ndx: Int, acc: String, param: KParameter ->
                acc + (if (ndx > 0) "\t\t" else "") + "queryParams.put(\"" + param.name + "\", String.valueOf(" + param.name + "));" + newline
            })
        } else {
            ""
        }
    }

    /**
     * builds a string of the parameters to put into the entity/body of request
     * e..g dataParams.put('id", id);
     */
    override fun buildDataParams(reg: Action): String {
        return if (reg.verb != Verb.Read) {
            reg.paramsUser.foldIndexed("", { ndx: Int, acc: String, param: KParameter ->
                acc + (if (ndx > 0) "\t\t" else "") + "postData.put(\"" + param.name + "\", " + param.name + ");" + newline
            })
        } else {
            ""
        }
    }
}
