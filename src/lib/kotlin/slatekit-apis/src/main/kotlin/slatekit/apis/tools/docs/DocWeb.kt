package slatekit.apis.tools.docs

import slatekit.apis.core.Action
import slatekit.apis.core.Api
import slatekit.common.console.SemanticWrites
import slatekit.common.console.SemanticWeb

/**
 * Generates help docs on the console.
 */
class DocWeb : Doc() {
    override val pathSeparator = "/"
    override val helpSuffix = "help"
    override val helpSeparator = "/"

    override val writer: SemanticWrites = SemanticWeb()

    override fun toString(): String = writer.toString()



     fun actionDetails(api: Api, action: Action, name: String) {
        with(writer) {
            tab(1)
            subTitle(getFormattedText(name, (docSettings.maxLengthAction ) + 3), endLine = false)
            keyValue("desc", action.desc, false)
            keyValue("verb", action.verb.name, false)
            keyValue("roles", action.roles.all.joinToString(","), false)
            keyValue("proto", action.sources.all.joinToString(","), false)
        }
    }
}
