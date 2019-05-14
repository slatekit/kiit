package slatekit.setup

import slatekit.common.Context
import slatekit.common.Uris
import slatekit.common.utils.Props
import slatekit.results.Success
import slatekit.results.Try
import java.io.File

class SetupService(val context: Context) {

    fun app(setupCtx:SetupContext): Try<String> {
        // Normalize/Canonical names
        val ctx = setupCtx.normalize()

        // Get root directory of destination
        val rootRaw = Uris.interpret(ctx.destination) ?: ""
        val root = rootRaw.replace("~", "/Users/kishore.reddy")
        val appDir = File(root, ctx.name)
        println(appDir.absolutePath)
        println(appDir.canonicalPath)
        println(appDir.toString())

        // Rewrite the context
        val finalCtx = setupCtx.copy(destination = appDir.toString())
        // Build the templates
        val actions = SetupTemplates.app()
        val rootDirAction = actions.first { it is Dir && it.root } as Dir
        val packageDirs = buildDirs(ctx, rootDirAction)
        val indexRoot = actions.indexOf(rootDirAction) + 1
        val before = actions.subList(0, indexRoot)
        val after = actions.subList(indexRoot, actions.size)
        val allActions = before.plus(packageDirs).plus(after)
        val template = SetupTemplate(finalCtx, appDir, allActions)

        // Create destination directory
        val creator = SetupCreator(template)
        val dest = creator.create(appDir.toString(), false)

        // Create files/folders
        template.actions.forEach {
            when(it) {
                is Dir   -> creator.dir(dest, it)
                is Conf  -> creator.conf(dest, it)
                is Build -> creator.build(dest, it)
                is Code  -> creator.code(dest, it)
                else     -> println("unknown action : " + it.toString())
            }
        }
        return Success("")
    }


    private fun buildDirs(ctx:SetupContext, dir:Dir):List<Dir> {
        val actionsWithPackage = ctx.packageName.split(".")
        val dirs = mutableListOf<Dir>()
        actionsWithPackage.forEachIndexed { index, s ->
            if(index == 0) {
                dirs.add(Dir(dir.path + Props.pathSeparator + s))
            } else {
                val fullPath = actionsWithPackage.joinToString(Props.pathSeparator, limit = index + 1)
                dirs.add(Dir(dir.path + Props.pathSeparator + fullPath))
            }
        }
        return dirs.toList()
    }
}
