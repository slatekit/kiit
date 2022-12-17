package kiit.app

import slatekit.common.Types
import slatekit.common.args.Args
import slatekit.common.args.ArgsSchema
import slatekit.utils.writer.ConsoleWriter
import slatekit.common.envs.Envs
import slatekit.common.info.About
import slatekit.common.info.Info
import slatekit.common.io.Alias
import slatekit.results.*
import slatekit.results.builders.Tries

/**
 * Builds the display for help / version / about requests.
 *  ==================================================
 *  ABOUT:
 *  area         slatekit
 *  name         Slate Kit
 *  desc         Slate Kit CLI for creating projects and access to other tools
 *  version      1.0.0
 *  tags         sample, template, app
 *  region       NY
 *  contact      #slatekit-team | slatekit-support@company.com
 *  url          www.slatekit.life
 *
 *  __________________________________________________
 *  OPTIONS:
 *  -env       = the environment to run in
 *                  ? optional  [String]  e.g. dev
 *  -region    = the region linked to app
 *                  ? optional  [String]  e.g. us
 *  -log.level = the log level for logging
 *                  ? optional  [String]  e.g. info
 *
 * __________________________________________________
 *  USAGE:
 *  - format  : java -jar app-name.jar -key=value*
 *  - example : java -jar slatekit.jar -env="dev" -region="us" -log.level="info"
 *
 *  __________________________________________________
 *  ENVS:
 *  -loc  : dev - Dev environment (local)
 *  -dev  : dev - Dev environment (shared)
 *  -qa1  : qat - QA environment  (current release)
 *  -pro  : pro - LIVE environment
 *
 *  ==================================================
 */
class AppHelp(val info: Info, val args: ArgsSchema, val envs: Envs = Envs.defaults()) {
    private val about = info.about
    private val writer = ConsoleWriter()

    companion object {
        fun process(cls:Class<*>, alias: Alias, rawArgs: List<String>, args: Args, about: About, schema: ArgsSchema?, envs: Envs = Envs.defaults()): Try<Args> {
            val assist = isAssist(rawArgs)
            return when (assist) {
                is Failure -> Success(args)
                is Success -> {
                    // Delegate help to the AppMeta component for (help | version | about )
                    val build = AppBuilder.build(cls, args, alias)
                    val appMeta = AppHelp(Info.of(about).copy(build = build), schema ?: AppBuilder.schema(), envs)
                    appMeta.handle(assist)

                    // Prevent futher processing by return failure
                    Tries.errored<Args>(Exception(assist.desc), Codes.ERRORED.copy(assist.status.name, assist.status.code, assist.status.desc))
                }
            }
        }


        fun isAssist(rawArgs: List<String>): Outcome<String> = AppUtils.isMetaCommand(rawArgs.toList())
    }

    /**
     * Checks the command line arguments for help, exit, or invalid arguments based on schema.
     *
     * @param rawArgs : the raw command line arguments directly from shell/console.
     * @param schema : the argument schema that defines what arguments are supported.
     * @return
     */
    fun handle(check: Outcome<String>) {
        if (check.success) {
            when (check.code) {
                Codes.HELP.code -> help()
                Codes.ABOUT.code -> about()
                Codes.VERSION.code -> version()
                else -> println("Unexpected command: " + check.desc)
            }
        }
    }

    private fun help() {
        wrap {
            title("About")
            writer.text("area         " + about.area)
            writer.text("name         " + about.name)
            writer.text("desc         " + about.desc)
            writer.text("version      " + info.build.version)
            options()
            usage()
            envs()
        }
    }

    private fun about() {
        wrap {
            title("About")
            writer.text("area         " + about.area)
            writer.text("name         " + about.name)
            writer.text("desc         " + about.desc)
            writer.text("tags         " + about.tags)
            writer.text("region       " + about.region)
            writer.text("contact      " + about.contact)
            writer.text("url          " + about.url)
            options()
            usage()
            envs()
        }
    }

    private fun usage() {
        section("Usage") {
            writer.highlight("format", false)
            writer.text("  : java -jar app-name.jar -key=value*", true)
            writer.highlight("example", false)
            writer.text(" : java -jar ${about.name.toLowerCase().replace(" ", "")}.jar ", false)
            args.items.forEach { arg ->
                val example = when (arg.dataType) {
                    Types.JStringClass.simpleName -> "\"${arg.example}\""
                    else -> arg.example
                }
                val display = "-${arg.name}=$example "
                writer.text(display, false)
            }
            println()
        }
    }

    private fun version() {
        println("version : " + info.build.version)
    }

    private fun options() {
        section("Options") {
            args.buildHelp()
        }
    }

    private fun envs() {
        section("Envs") {
            envs.all.forEach { env ->
                writer.highlight("${env.name}", false)
                writer.text("  : ${env.mode.name} - ${env.desc}")
            }
        }
    }

    private fun wrap(op: () -> Unit) {
        writer.text(separator("="))
        op()
        println()
        writer.text(separator("="))
        writer.text("")
    }

    private fun section(title: String, op: () -> Unit) {
        println()
        writer.text(separator("_"))
        title(title)
        op()
    }

    private fun title(name: String) = writer.title(name.toUpperCase() + ": ")

    private fun separator(letter: String): String = letter.repeat(50)
}
