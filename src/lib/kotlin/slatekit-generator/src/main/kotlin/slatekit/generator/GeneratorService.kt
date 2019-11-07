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
        val targetDir = File(root)
        log(targetDir)

        // Rewrite the context
        val finalCtx = setupCtx.copy(destination = targetDir.toString())

        // Create target dir
        targetDir.mkdir()

        // Execute the dependencies first
        template.requires.forEach { execute(finalCtx, it, targetDir) }

        // Execute the template actions
        execute(finalCtx, template, targetDir)

        return Success("")
    }


    private fun execute(ctx: GeneratorContext, template: Template, targetDir:File) {
        val templateRootDirAction = template.actions.firstOrNull { it is Action.MkDir && it.root } as Action.MkDir?
        val finalTargetDir = when(templateRootDirAction) {
            null -> targetDir
            else -> File(targetDir, templateRootDirAction.path)
        }
        val creator = Creator(context, ctx, template, cls)
        logger.info("")
        logger.info("EXECUTING ===============================")
        logger.info("target.dir: ${finalTargetDir.absolutePath}")
        logger.info("template.name: ${template.name}")
        logger.info("template.path: ${template.dir.absolutePath}")

        template.actions.forEach {
            when(it) {
                is Action.MkDir -> {
                    logger.info("Action: type=MkDir, path=${it.path}")
                    creator.dir(targetDir, it)
                }
                is Action.Copy -> {
                    logger.info("Action: type=Copy, source=${it.source}, target=${it.target}")
                    val copy = it
                    val action = if(it.fileType == FileType.Code) {
                        val copyFinalTargetPath = creator.replace(copy.target)
                        val finalAction = copy.copy(target = copyFinalTargetPath)
                        creator.makeDirs(finalTargetDir, finalAction.target, { it.dropLast(1)})
                        finalAction
                    }
                    else {
                        it
                    }
                    creator.copy(finalTargetDir, action)
                }
            }
        }
    }


    private fun log(appDir:File) {
        logger.info(appDir.absolutePath)
        logger.info(appDir.canonicalPath)
        logger.info(appDir.toString())
    }
}
