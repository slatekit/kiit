package slatekit.setup

import slatekit.common.Context
import slatekit.common.Uris
import slatekit.results.Success
import slatekit.results.Try
import java.io.File

class SetupService(val context: Context) {

    fun app(setupCtx:SetupContext): Try<String> {

        // Get root directory of destination
        val rootRaw = Uris.interpret(setupCtx.destination) ?: ""
        val root = rootRaw.replace("~", "/Users/kishore.reddy")
        val appDir = File(root, setupCtx.name)
        println(appDir.absolutePath)
        println(appDir.canonicalPath)
        println(appDir.toString())

        // Rewrite the context
        val ctx = setupCtx.copy(destination = appDir.toString())
        // Build the templates
        val actions = SetupTemplates.app()
        val template = SetupTemplate(ctx, appDir, actions)

        // Create destination directory
        val creator = SetupCreator(template)
        val dest = creator.create(appDir.toString(), false)

        // Create files/folders
        template.actions.forEach {
            when(it) {
                is Dir   -> creator.dir(dest, it)
                is Conf  -> creator.conf(dest, it)
                is Build -> creator.build(dest, it)
                else     -> println("unknown action : " + it.toString())
            }
        }
        return Success("")
    }
}
