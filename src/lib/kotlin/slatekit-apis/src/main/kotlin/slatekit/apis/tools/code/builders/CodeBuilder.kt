package slatekit.apis.tools.code.builders

import slatekit.apis.core.Action
import slatekit.apis.tools.code.TypeInfo
import slatekit.common.newline
import slatekit.meta.KTypes
import slatekit.meta.Reflector
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType

interface CodeBuilder {

    val basicTypes: Map<KType, TypeInfo>

    /**
     * builds a string of parameters to put into the query string.
     * e.g. queryParams.put("id", id);
     */
    fun buildQueryParams(reg: Action): String

    /**
     * builds a string of the parameters to put into the entity/body of request
     * e..g dataParams.put('id", id);
     */
    fun buildDataParams(reg: Action): String

    /**
     * builds an individual argument to the method
     */
    fun buildArg(parameter: KParameter): String

    /**
     * builds all the properties/fields
     */
    fun buildModelInfo(cls: KClass<*>): String

    /**
     * builds the name of the datatype for the target(Java) language.
     */
    /**
     * builds the name of the datatype for the target(Java) language.
     */
    fun buildTypeName(tpe: KType): TypeInfo {
        return if (basicTypes.containsKey(tpe)) {
            basicTypes[tpe]!!
        } else {
            val cls = tpe.classifier as KClass<*>
            if (Reflector.isSlateKitEnum(cls)) {
                buildTypeName(KTypes.KIntType)
            } else if (cls == slatekit.results.Result::class) {
                val genType = tpe.arguments[0].type!!
                val finalType = buildTypeName(genType)
                finalType
            } else if (cls.supertypes.contains(KTypes.KSmartValueType)) {
                TypeInfo(true, false, "String", "String", KTypes.KSmartValueClass, KTypes.KSmartValueClass, "String.class")
            } else if (cls == List::class) {
                val listType = tpe.arguments[0].type!!
                val listCls = KTypes.getClassFromType(listType)
                val listTypeInfo = buildTypeName(listType)
                val typeSig = "List<" + listTypeInfo.targetReturnType + ">"
                TypeInfo(false, true, typeSig, typeSig, List::class, listCls, listTypeInfo.conversionType)
            } else if (cls == Map::class) {
                val tpeKey = tpe.arguments[0].type!!
                val tpeVal = tpe.arguments[1].type!!
                // val clsKey = KTypes.getClassFromType(tpeKey)
                val clsVal = KTypes.getClassFromType(tpeVal)
                val keyTypeInfo = buildTypeName(tpeKey)
                val valTypeInfo = buildTypeName(tpeVal)
                val sig = "Map<" + keyTypeInfo.targetReturnType + "," + valTypeInfo.targetReturnType + ">"
                TypeInfo(false, true, sig, sig, Map::class, clsVal, "${keyTypeInfo.conversionType},${valTypeInfo.conversionType}")
            } else if (cls == Pair::class) {
                val tpeFirst = tpe.arguments[0].type!!
                val tpeSecond = tpe.arguments[1].type!!
                val firstTypeInfo = buildTypeName(tpeFirst)
                val secondTypeInfo = buildTypeName(tpeSecond)
                val sig = "Pair<" + firstTypeInfo.targetReturnType + "," + secondTypeInfo.targetReturnType + ">"
                TypeInfo(false, false, sig, sig, cls, cls, "${firstTypeInfo.conversionType},${secondTypeInfo.conversionType}")
            } else {
                val sig = cls.simpleName ?: ""
                TypeInfo(false, false, sig, sig, cls, cls, sig + ".class")
            }
        }
    }


    fun <T> collect(items:List<T>, tab:String, sep:String?, call:(T) -> String):String {
        return items.foldIndexed("") { ndx: Int, acc: String, param: T ->
            val isLast = ndx == items.size - 1
            val separator = if(sep != null && isLast) sep else null
            val endLine = if(isLast) "" else newline
            acc + (if (ndx > 0) tab else "") + call(param) + separator + endLine
        }
    }
}
