package kiit.apis.tools.code.builders

import kiit.apis.routes.Action
import kiit.apis.tools.code.TypeInfo
import slatekit.common.newline
import slatekit.meta.KTypes
import slatekit.meta.Reflector
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType

interface CodeBuilder {

    val basicTypes: Map<KType, TypeInfo>
    val mapTypeDecl:String

    /**
     * builds a string of parameters to put into the query string.
     * e.g. queryParams.put("id", id);
     */
    fun buildQueryParams(action: Action): String

    /**
     * builds a string of the parameters to put into the entity/body of request
     * e..g dataParams.put('id", id);
     */
    fun buildDataParams(action: Action): String

    /**
     * Builds the arguments
     */
    fun buildArgs(action: Action): String {
        return collect(action.paramsUser, "\t\t", ",", true) { buildArg(it) }
    }

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
                TypeInfo(true, false, listOf(KTypes.KSmartValueClass), null, this::buildTargetName)
            } else if (cls == List::class) {
                val types = listOf(tpe.arguments[0].type!!)
                return TypeInfo(false, true, types.map { it.classifier as KClass<*> }, List::class, this::buildTargetName)
            } else if (cls == Map::class) {
                val types = listOf(tpe.arguments[0].type!!, tpe.arguments[1].type!!)
                return TypeInfo(false, true, types.map { it.classifier as KClass<*> }, Map::class, this::buildTargetName)
            } else if (cls == Pair::class) {
                val types = listOf(tpe.arguments[0].type!!, tpe.arguments[1].type!!)
                return TypeInfo(false, true, types.map { it.classifier as KClass<*> }, Pair::class, this::buildTargetName)
            } else {
                TypeInfo(false, false, listOf(cls), null, this::buildTargetName)
            }
        }
    }

    fun buildTypeLoader():String


    fun buildTargetName(cls:KClass<*>): String = cls.simpleName!!


    fun <T> collect(items:List<T>, tabs:String, sep:String?, forceSepator:Boolean, call:(T) -> String):String {
        return items.foldIndexed("") { ndx: Int, acc: String, param: T ->
            val isLast = ndx == items.size - 1
            val separator = if(forceSepator || (sep != null && !isLast) ) sep else ""
            val endLine = if(isLast) "" else newline
            acc + (if (ndx > 0) tabs else "") + call(param) + separator + endLine
        }
    }
}
