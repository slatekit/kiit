package slatekit.generator

import slatekit.common.Context
import slatekit.common.Uris
import slatekit.results.Success
import slatekit.results.Try
import java.io.File

class GeneratorService(val context: Context, val cls:Class<*>) {

    val logger = context.logs.getLogger()


    fun generate(setupCtx: GeneratorContext, template: Template): Try<String> {
        // Normalize/Canonical names
        val ctx = setupCtx.normalize()

        // Get root directory of destination
        val root = Uris.interpret(ctx.destination) ?: ""
        val targetDir = File(root, ctx.name)
        log(targetDir)

        // Rewrite the context
        val finalCtx = setupCtx.copy(destination = targetDir.toString())

        // Execute the dependencies first
        template.requires.forEach { execute(finalCtx, it, targetDir) }

        // Execute the template actions
        execute(finalCtx, template, targetDir)

        return Success("")
    }


    private fun execute(ctx: GeneratorContext, template: Template, rootDir:File) {
        val templateRootDirAction = template.actions.firstOrNull { it is Action.MkDir && it.root } as Action.MkDir?
        val targetDir = when(templateRootDirAction) {
            null -> rootDir
            else -> File(rootDir, templateRootDirAction.path)
        }
        val creator = Creator(context, ctx, template, cls)
        logger.info("Template")
        logger.info("name: ${template.name}")
        logger.info("path: ${template.dir.absolutePath}")

        template.actions.forEach {
            when(it) {
                is Action.MkDir -> {
                    logger.info("Action: type=MkDir, path=${it.path}")
                    creator.dir(targetDir, it)
                }
                is Action.Copy -> {
                    logger.info("Action: type=Copy, source=${it.source}, target=${it.target}")
                    creator.copy(targetDir, it)
                }
            }
        }
    }


    /**
     * Build a list of [Action.Dir] actions to create directories based on package name.
     */
    private fun makeDirs(targetDir:File, dir: Action.MkDir):File {
        val parts = dir.path.split("/")
        val finalPath = parts.reduce { acc, curr ->
            File(targetDir, curr).mkdir()
            "$acc/$curr"
        }
        val finalDir = File(finalPath)
        finalDir.mkdir()
        return finalDir
    }


    private fun log(appDir:File) {
        logger.info(appDir.absolutePath)
        logger.info(appDir.canonicalPath)
        logger.info(appDir.toString())
    }
}
