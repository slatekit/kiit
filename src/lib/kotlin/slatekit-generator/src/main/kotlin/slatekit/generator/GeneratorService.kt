package slatekit.generator

import kiit.common.conf.Conf
import kiit.common.log.Logger
import kiit.utils.writer.ConsoleWriter
import kiit.context.Context
import kiit.results.Success
import kiit.results.Try
import kiit.results.builders.Tries
import java.io.File

/**
 * @param context  : Startup context
 * @param conf     : Conf containing settings / path to templates
 * @param cls      : Class containing the startup resources ( if applicable )
 * @param settings : Settings for the generator
 */
class GeneratorService(val context: Context,
                       val conf: Conf,
                       val cls:Class<*>,
                       val settings: GeneratorSettings,
                       val logger: Logger = context.logs.getLogger()
                        ) {

    val currentDir = System.getProperty("user.dir")

    fun generate(setupCtx: GeneratorContext, template: Template): Try<GeneratorResult> {
        return Tries.of {
            // Normalize/Canonical names
            val ctx = setupCtx.normalize(settings)

            // Get root directory of destination
            log(ctx.destDir)

            // Rewrite the context
            val finalCtx = setupCtx.copy(destDir = ctx.destDir)

            // Create target dir
            ctx.destDir.mkdir()

            // Target dir = dest/${name}
            // e.g. ~/slatekit/gen/MyApp1 or CURRENT_DIR/MyApp1
            val targetRoot = File(ctx.destDir.absolutePath)
            val targetDir = File(ctx.destDir.absolutePath, setupCtx.name)
            logger.info("creating : ${targetRoot.absolutePath}")
            logger.info("creating : ${targetDir.absolutePath}")
            targetRoot.mkdir()
            targetDir.mkdir()

            // Execute the dependencies first
            val writer = ConsoleWriter()
            writer.text("")
            template.requires.forEach { execute(finalCtx, it, targetDir) }

            // Execute the template actions
            execute(finalCtx, template, targetDir)
            writer.text("Created project, check log file for more details.")
            GeneratorResult("Created ${setupCtx.name}", targetDir.absolutePath, template.path.absolutePath, setupCtx.settings.tool.logFile)
        }
    }


    private fun execute(ctx: GeneratorContext, template: Template, targetDir:File) {
        val templateRootDirAction = template.actions.firstOrNull { it is Action.MkDir && it.root } as Action.MkDir?
        val finalTargetDir = when(templateRootDirAction) {
            null -> targetDir
            else -> File(targetDir, templateRootDirAction.path)
        }
        val creator = Creator(context, ctx, template, cls, logger)
        logger.info("")
        logger.info("template.name=${template.name}, template.path: ${template.dir.absolutePath}, target.dir=${finalTargetDir.absolutePath}")

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
