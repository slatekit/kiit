package slatekit.apis.tools.docs

import slatekit.apis.core.Action
import slatekit.apis.core.Api
import slatekit.common.console.Writer
import slatekit.common.console.WebWriter

/**
 * Generates help docs on the console.
 */
class DocWeb : Doc() {
    override val pathSeparator = "/"
    override val helpSuffix = "help"
    override val helpSeparator = "/"

    override val writer: Writer = WebWriter()

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
