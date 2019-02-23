package slatekit.core.app

import slatekit.common.ABOUT
import slatekit.common.HELP
import slatekit.common.VERSION
import slatekit.common.args.ArgsSchema
import slatekit.core.common.AppContext
import slatekit.results.Notice

class AppMetadata(val ctx:AppContext, val args:ArgsSchema) {
    
    /**
     * Checks the command line arguments for help, exit, or invalid arguments based on schema.
     *
     * @param rawArgs : the raw command line arguments directly from shell/console.
     * @param schema : the argument schema that defines what arguments are supported.
     * @return
     */
    fun handle( check:Notice<String>) {
        if (check.success) {
            when(check.code) {
                HELP.code    -> help()
                ABOUT.code   -> about()
                VERSION.code -> version()
                else         -> println("Unexpected command: " + check.msg)
            }
        }
    }


    private fun help() {
        println("app.name         " + ctx.app.name)
        println("app.desc         " + ctx.app.desc)
        println("app.version      " + ctx.app.version)
        println(args.buildHelp())
    }


    private fun about() {
        println("app.name         " + ctx.app.name)
        println("app.desc         " + ctx.app.desc)
        println("app.version      " + ctx.app.version)
        println("app.tags         " + ctx.app.tags)
        println("app.group        " + ctx.app.group)
        println("app.region       " + ctx.app.region)
        println("app.contact      " + ctx.app.contact)
        println("app.url          " + ctx.app.url)
        println(args.buildHelp())
    }


    private fun version() {
        println(ctx.app.version)
    }
}