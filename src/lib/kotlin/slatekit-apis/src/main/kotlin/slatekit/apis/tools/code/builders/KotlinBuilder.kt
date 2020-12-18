package slatekit.apis.tools.code.builders

import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import slatekit.apis.Verb
import slatekit.apis.routes.Action
import slatekit.apis.tools.code.CodeGenSettings
import slatekit.apis.tools.code.TypeInfo
import slatekit.common.newline
import slatekit.meta.Reflector

class KotlinBuilder(val settings: CodeGenSettings) : CodeBuilder {


    override val basicTypes = TypeInfo.basicTypes
    override val mapTypeDecl = "val postData = MutableMap<String, Any>()"

    override fun buildModelInfo(cls: KClass<*>): String {

        val props = Reflector.getProperties(cls)
        val fields = props.foldIndexed("") { ndx: Int, acc: String, prop: KProperty<*> ->
            val type = prop.returnType
            val typeInfo = buildTypeName(type)
            val suffix = if (ndx < props.size - 1) "," else ""
            val field = "val " + prop.name + " : " + typeInfo.targetType + suffix + newline
            acc + (if (ndx > 0) "\t" else "") + field
        }
        return fields
    }

    override fun buildArg(parameter: KParameter): String {
        return parameter.name + " : " + buildTypeName(parameter.type).targetTypeName
    }

    /**
     * builds a string of parameters to put into the query string.
     * e.g. "id" to id
     */
    override fun buildQueryParams(action: Action): String {
        return when(action.verb) {
            Verb.Get -> collect(action.paramsUser, "\t\t\t", ",", false) { "\"" + it.name + "\" to " + it.name + ".toString()" }
            else -> ""
        }
    }

    /**
     * builds a string of the parameters to put into the entity/body of request
     * e..g dataParams.put('id", id);
     */
    override fun buildDataParams(action: Action): String {
        return when(action.verb) {
            Verb.Get -> ""
            else     -> collect(action.paramsUser, "\t\t\t", ",", false) { "\"" + it.name + "\" to " + it.name }
        }
    }


    override fun buildTypeLoader(): String = "::class.java"
}
