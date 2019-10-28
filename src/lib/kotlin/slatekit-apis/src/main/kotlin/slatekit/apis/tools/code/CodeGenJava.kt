package slatekit.apis.tools.code

import slatekit.apis.Verb
import slatekit.apis.core.Action
import slatekit.apis.Verbs
import slatekit.common.newline
import slatekit.meta.KTypes
import slatekit.meta.Reflector
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty

class CodeGenJava(settings: CodeGenSettings) : CodeGenBase(settings) {

    override val basicTypes = listOf(
            // Basic types
            Pair(KTypes.KStringType, TypeInfo(true, false, "String", "String", KTypes.KStringClass, KTypes.KStringClass, "String" + ".class")),
            Pair(KTypes.KBoolType, TypeInfo(true, false, "boolean", "Boolean", KTypes.KBoolClass, KTypes.KBoolClass, "Boolean" + ".class")),
            Pair(KTypes.KShortType, TypeInfo(true, false, "short", "Short", KTypes.KShortClass, KTypes.KShortClass, "Short" + ".class")),
            Pair(KTypes.KIntType, TypeInfo(true, false, "int", "Integer", KTypes.KIntClass, KTypes.KIntClass, "Integer" + ".class")),
            Pair(KTypes.KLongType, TypeInfo(true, false, "long", "Long", KTypes.KLongClass, KTypes.KLongClass, "Long" + ".class")),
            Pair(KTypes.KFloatType, TypeInfo(true, false, "float", "Float", KTypes.KFloatClass, KTypes.KFloatClass, "Float" + ".class")),
            Pair(KTypes.KDoubleType, TypeInfo(true, false, "double", "Double", KTypes.KDoubleClass, KTypes.KDoubleClass, "Double" + ".class")),
            Pair(KTypes.KDateTimeType, TypeInfo(true, false, "Date", "Date", KTypes.KDateTimeClass, KTypes.KDateTimeClass, "Date" + ".class")),
            Pair(KTypes.KLocalDateType, TypeInfo(true, false, "Date", "Date", KTypes.KLocalDateClass, KTypes.KLocalDateClass, "Date" + ".class")),
            Pair(KTypes.KLocalTimeType, TypeInfo(true, false, "Date", "Date", KTypes.KLocalTimeClass, KTypes.KLocalTimeClass, "Date" + ".class")),
            Pair(KTypes.KLocalDateTimeType, TypeInfo(true, false, "Date", "Date", KTypes.KLocalDateTimeClass, KTypes.KLocalDateTimeClass, "Date" + ".class")),
            Pair(KTypes.KZonedDateTimeType, TypeInfo(true, false, "Date", "Date", KTypes.KZonedDateTimeClass, KTypes.KZonedDateTimeClass, "Date" + ".class")),
            Pair(KTypes.KDocType, TypeInfo(true, false, "String", "String", KTypes.KDocClass, KTypes.KDocClass, "String" + ".class")),
            Pair(KTypes.KVarsType, TypeInfo(true, false, "String", "String", KTypes.KVarsClass, KTypes.KVarsClass, "String" + ".class")),
            Pair(KTypes.KSmartValueType, TypeInfo(true, false, "String", "String", KTypes.KSmartValueClass, KTypes.KSmartValueClass, "String" + ".class")),
            Pair(KTypes.KUniqueIdType, TypeInfo(true, false, "String", "String", KTypes.KUniqueIdClass, KTypes.KUniqueIdClass, "String" + ".class")),
            Pair(KTypes.KUUIDType, TypeInfo(true, false, "String", "String", KTypes.KUUIDClass, KTypes.KUUIDClass, "String" + ".class")),
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
            val field = "public " + typeInfo.targetParameterType + " " + prop.name + ";" + newline
            acc + (if (ndx > 0) "\t" else "") + field
        })
        return fields
    }

    override fun buildArg(parameter: KParameter): String {
        return buildTypeName(parameter.type).targetParameterType + " " + parameter.name
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
