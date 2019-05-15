package slatekit.generator

import slatekit.common.Context
import slatekit.common.Uris
import slatekit.common.utils.Props
import slatekit.results.Success
import slatekit.results.Try
import java.io.File

class GeneratorService(val context: Context) {

    fun generate(setupCtx:GeneratorContext, template: Template): Try<String> {
        // Normalize/Canonical names
        val ctx = setupCtx.normalize()

        // Get root directory of destination
        val root = Uris.interpret(ctx.destination) ?: ""
        val appDir = File(root, ctx.name)
        log(appDir)

        // Rewrite the context
        val finalCtx = setupCtx.copy(destination = appDir.toString())

        // Build the templates
        val actions = template.actions
        val rootDirAction = actions.first { it is Action.MkDir && it.root } as Action.MkDir
        val packageDirs = buildPackageDirs(ctx, rootDirAction)
        val indexRoot = actions.indexOf(rootDirAction) + 1
        val before = actions.subList(0, indexRoot)
        val after = actions.subList(indexRoot, actions.size)
        val allActions = before.plus(packageDirs).plus(after)
        val finalTemplate = template.copy(actions = allActions)

        // Execute the template actions
        execute(finalCtx, finalTemplate, appDir)

        return Success("")
    }


    private fun execute(context:GeneratorContext, template:Template, appDir:File) {
        val creator = Creator(context, template)
        val dest = creator.create(appDir.toString(), false)
        template.actions.forEach {
            when(it) {
                is Action.MkDir -> creator.dir(dest, it)
                is Action.Doc   -> creator.doc(dest, it)
                is Action.Conf  -> creator.conf(dest, it)
                is Action.Build -> creator.build(dest, it)
                is Action.Code  -> creator.code(dest, it)
            }
        }
    }


    /**
     * Build a list of [Action.Dir] actions to create directories based on package name.
     */
    private fun buildPackageDirs(ctx:GeneratorContext, dir:Action.MkDir):List<Action.MkDir> {
        val actionsWithPackage = ctx.packageName.split(".")
        val dirs = mutableListOf<Action.MkDir>()
        actionsWithPackage.forEachIndexed { index, s ->
            if(index == 0) {
                dirs.add(Action.MkDir(dir.path + Props.pathSeparator + s))
            } else {
                val fullPath = actionsWithPackage.joinToString(Props.pathSeparator, limit = index + 1)
                dirs.add(Action.MkDir(dir.path + Props.pathSeparator + fullPath))
            }
        }
        return dirs.toList()
    }


    private fun log(appDir:File) {
        println(appDir.absolutePath)
        println(appDir.canonicalPath)
        println(appDir.toString())
    }
}
