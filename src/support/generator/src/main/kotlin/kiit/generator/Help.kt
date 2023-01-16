package kiit.generator

import kiit.common.conf.Conf
import kiit.common.ext.orElse
import kiit.utils.writer.ConsoleWriter
import kiit.context.Context

class Help(val name:String) {


    /**
     * Shows just the welcome header
     */
    open fun intro(){
        val writer = ConsoleWriter()
        writer.text("**********************************************")
        writer.title("Welcome to $name")
        writer.text("You can use this CLI to create new Kiit projects")
        writer.text("**********************************************")
        writer.text("")
    }


    /**
     * Shows help info on how to run the generator
     */
    open fun show(op:(() -> Unit)? = null) {
        val writer = ConsoleWriter()

        intro()

        // Routing
        writer.title("OVERVIEW")
        writer.text("1. COMMANDS  : are Organized into 3 part ( AREAS, APIS, ACTIONS ) : {area}.{api}.{action}")
        writer.text("2. DISCOVERY : available using \"?\" as in \"area ?\" \"area.api ?\" \"area.api.action ?\"")
        writer.text("3. EXECUTE   : using 3 part name and passing inputs e.g. {area}.{api}.{action} -key=value*")
        writer.text("")

        op?.let {
            it.invoke()
            writer.text("")
        }

        examples()
    }


    /**
     * Shows diagnostics info about directory / versions used
     */
    open fun settings(ctx: Context, settings: Conf) {
        val writer = ConsoleWriter()
        val outputDir = settings.getString("generation.output"    ).orElse("CURRENT_DIR")
        writer.title("SETTINGS")
        writer.keyValue("system.currentDir      ",  System.getProperty("user.dir"))
        writer.keyValue("kiit.dir           ",  ctx.dirs?.pathToApp ?: "")
        writer.keyValue("kiit.settings      ",  java.io.File(ctx.dirs?.pathToConf, "settings.conf").absolutePath)
        writer.keyValue("kiit.tag           ",  ctx.conf.getString("kiit.tag"))
        writer.keyValue("kiit.version.cli   ",  ctx.conf.getString("kiit.version.cli" ))
        writer.keyValue("kiit.version       ",  settings.getString("kiit.version"     ))
        writer.keyValue("kiit.version.beta  ",  settings.getString("kiit.version.beta"))
        writer.keyValue("kiit.kotlin.version",  settings.getString("kotlin.version"       ))
        writer.keyValue("generation.source      ",  settings.getString("generation.source"    ))
        writer.keyValue("generation.output      ",  outputDir)
    }


    /**
     * Shows examples of usage
     */
    open fun examples(){
        val writer = ConsoleWriter()
        writer.title("EXAMPLES")
        writer.text("You can create the various Slate Kit Projects below")
        writer.highlight("1. kiit new app -name=\"MyApp1\" -packageName=\"company1.apps\"")
        writer.highlight("2. kiit new api -name=\"MyAPI1\" -packageName=\"company1.apis\"")
        writer.highlight("3. kiit new cli -name=\"MyCLI1\" -packageName=\"company1.apps\"")
        writer.highlight("4. kiit new env -name=\"MyApp2\" -packageName=\"company1.apps\"")
        writer.highlight("5. kiit new job -name=\"MyJob1\" -packageName=\"company1.jobs\"")
        writer.text("")
    }


    /**
     * Shows examples of usage
     */
    open fun exit(){
        val writer = ConsoleWriter()
        writer.failure("Type \"exit\" to exit app")
        writer.text("")
    }
}