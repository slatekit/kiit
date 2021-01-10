package slatekit.generator

import slatekit.common.conf.Conf
import slatekit.common.ext.orElse
import slatekit.common.writer.ConsoleWriter
import slatekit.context.Context

object Help {

    /**
     * Shows help info on how to run the generator
     */
    fun help(name:String, op:(() -> Unit)?) {
        val writer = ConsoleWriter()

        writer.text("**********************************************")
        writer.title("Welcome to $name")
        writer.text("You can use this CLI to create new Slate Kit projects")
        writer.text("**********************************************")
        writer.text("")

        // Routing
        writer.title("OVERVIEW")
        writer.text("1. COMMANDS  : are Organized into 3 part ( AREAS, APIS, ACTIONS ) : {area}.{api}.{action}")
        writer.text("2. DISCOVERY : available using \"?\" as in \"area ?\" \"area.api ?\" \"area.api.action ?\"")
        writer.text("3. EXECUTE   : using 3 part name and passing inputs e.g. {area}.{api}.{action} -key=value*")
        writer.text("")

        op?.invoke()

        writer.title("EXAMPLE")
        writer.text("You can create the various Slate Kit Projects below")
        writer.highlight("1. slatekit new app -name=\"MyApp1\" -package=\"company1.apps\"")
        writer.highlight("2. slatekit new api -name=\"MyAPI1\" -package=\"company1.apis\"")
        writer.highlight("3. slatekit new cli -name=\"MyCLI1\" -package=\"company1.apps\"")
        writer.highlight("4. slatekit new env -name=\"MyApp2\" -package=\"company1.apps\"")
        writer.highlight("5. slatekit new job -name=\"MyJob1\" -package=\"company1.jobs\"")
        writer.text("")
    }


    /**
     * Shows diagnostics info about directory / versions used
     */
    fun info(ctx: Context, settings: Conf) {
        val writer = ConsoleWriter()
        val outputDir = settings.getString("generation.output"    ).orElse("CURRENT_DIR")
        writer.title("SETTINGS")
        writer.keyValue("system.currentDir      ",  System.getProperty("user.dir"))
        writer.keyValue("slatekit.dir           ",  ctx.dirs?.pathToApp ?: "")
        writer.keyValue("slatekit.settings      ",  java.io.File(ctx.dirs?.pathToConf, "settings.conf").absolutePath)
        writer.keyValue("slatekit.tag           ",  ctx.conf.getString("slatekit.tag"))
        writer.keyValue("slatekit.version       ",  settings.getString("slatekit.version"     ))
        writer.keyValue("slatekit.version.beta  ",  settings.getString("slatekit.version.beta"))
        writer.keyValue("slatekit.kotlin.version",  settings.getString("kotlin.version"       ))
        writer.keyValue("generation.source      ",  settings.getString("generation.source"    ))
        writer.keyValue("generation.output      ",  outputDir)
    }



}