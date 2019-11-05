package slatekit.apis.tools.docs

import slatekit.apis.core.Action
import slatekit.apis.core.Api
import slatekit.common.console.SemanticWrites
import slatekit.common.console.WebWriter

/**
 * Generates help docs on the console.
 */
class DocWeb : Doc() {
    override val pathSeparator = "/"
    override val helpSuffix = "help"
    override val helpSeparator = "/"

    override val writer: SemanticWrites = WebWriter()

    override fun toString(): String = writer.toString()

    override fun onApiBegin(api: Api, options: ApiVisitOptions?) {
        with(writer) {
            title(getFormattedText(api.area + "/" + api.name, (options?.maxLength ?: 0) + 3), endLine = false)
            keyValue("desc", api.desc, false)
            keyValue("route", "${api.area}/${api.name}", false)
            keyValue("area", api.area, false)
            keyValue("name", api.name, false)
            keyValue("verb", api.verb.name, false)
            keyValue("roles", api.roles.all.joinToString(","), false)
            keyValue("proto", api.sources.all.joinToString(","), false)
            line()
            line()
        }
    }

    override fun onApiActionBegin(api: Api, action: Action, name: String, options: ApiVisitOptions?) {
        with(writer) {
            tab(1)
            subTitle(getFormattedText(name, (options?.maxLength ?: 0) + 3), endLine = false)
            keyValue("desc", action.desc, false)
            keyValue("verb", action.verb.name, false)
            keyValue("roles", action.roles.all.joinToString(","), false)
            keyValue("proto", action.sources.all.joinToString(","), false)
        }
    }
}
