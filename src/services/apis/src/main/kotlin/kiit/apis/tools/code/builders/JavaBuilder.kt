package kiit.apis.tools.code.builders

import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kiit.apis.Verb
import kiit.apis.routes.Action
import kiit.apis.tools.code.CodeGenSettings
import kiit.apis.tools.code.TypeInfo
import kiit.common.newline
import kiit.meta.KTypes
import kiit.meta.Reflector

class JavaBuilder(val settings: CodeGenSettings) : CodeBuilder {

    override val basicTypes = TypeInfo.basicTypes
    override val mapTypeDecl = "HashMap<String, Object> postData = new HashMap<>();"

    override fun buildModelInfo(cls: KClass<*>): String {
        val props = Reflector.getProperties(cls)
        val fields = props.foldIndexed("") { ndx: Int, acc: String, prop: KProperty<*> ->
            val type = prop.returnType
            val typeInfo = buildTypeName(type)
            val field = "public " + typeInfo.targetType + " " + prop.name + ";" + newline
            acc + (if (ndx > 0) "\t" else "") + field
        }
        return fields
    }

    override fun buildArg(parameter: KParameter): String {
        return buildTargetName(parameter.type.classifier as KClass<*>) + " " + parameter.name
    }

    /**
     * builds a string of parameters to put into the query string.
     * e.g. queryParams.put("id", id);
     */
    override fun buildQueryParams(action: Action): String {
        return if (action.verb == Verb.Get) {
//            action.paramsUser.foldIndexed("") { ndx: Int, acc: String, param: KParameter ->
//                acc + (if (ndx > 0) "\t\t" else "") + "queryParams.put(\"" + param.name + "\", String.valueOf(" + param.name + "));" + newline
//            }
            ""
        } else {
            ""
        }
    }

    /**
     * builds a string of the parameters to put into the entity/body of request
     * e..g dataParams.put('id", id);
     */
    override fun buildDataParams(action: Action): String {
        return if (action.verb != Verb.Get) {
//            action.paramsUser.foldIndexed("") { ndx: Int, acc: String, param: KParameter ->
//                acc + (if (ndx > 0) "\t\t" else "") + "postData.put(\"" + param.name + "\", " + param.name + ");" + newline
//            }
            ""
        } else {
            ""
        }
    }


    override fun buildTargetName(cls:KClass<*>): String {
        return when(cls) {
            KTypes.KStringClass     ->  KTypes.KStringClass    .java.simpleName
            KTypes.KBoolClass       ->  KTypes.KBoolClass      .java.simpleName
            KTypes.KShortClass      ->  KTypes.KShortClass     .java.simpleName
            KTypes.KIntClass        ->  KTypes.KIntClass       .java.simpleName
            KTypes.KLongClass       ->  KTypes.KLongClass      .java.simpleName
            KTypes.KFloatClass      ->  KTypes.KFloatClass     .java.simpleName
            KTypes.KDoubleClass     ->  KTypes.KDoubleClass    .java.simpleName
            KTypes.KSmartValueClass ->  KTypes.KSmartValueClass.java.simpleName
            KTypes.KDecStringClass  ->  KTypes.KStringClass    .java.simpleName
            KTypes.KDecIntClass     ->  KTypes.KStringClass    .java.simpleName
            KTypes.KDecLongClass    ->  KTypes.KStringClass    .java.simpleName
            KTypes.KDecDoubleClass  ->  KTypes.KStringClass    .java.simpleName
            else                    ->  KTypes.KStringClass    .java.simpleName
        }
    }

    override fun buildTypeLoader(): String = ".class"
}
