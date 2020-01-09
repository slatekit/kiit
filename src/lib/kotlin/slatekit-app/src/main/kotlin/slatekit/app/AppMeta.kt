package slatekit.app

import slatekit.common.args.Args
import slatekit.common.args.ArgsSchema
import slatekit.common.info.About
import slatekit.results.*
import slatekit.results.builders.Tries

class AppMeta(val about: About, val args: ArgsSchema) {

    companion object {
        fun process(rawArgs: List<String>, args: Args, about: About, schema: ArgsSchema?): Try<Args> {
            val isHelp = AppUtils.isMetaCommand(rawArgs.toList())
            return when(isHelp){
                is Failure -> Success(args)
                is Success -> {
                    // Delegate help to the AppMeta component for (help | version | about )
                    val appMeta = AppMeta(about, schema ?: AppBuilder.schema())
                    appMeta.handle(isHelp)

                    // Prevent futher processing by return failure
                    Tries.errored<Args>(Exception(isHelp.msg), Codes.ERRORED.copy(isHelp.status.code, isHelp.status.msg))
                }
            }
//            return if (isHelp.success) {
//                // Delegate help to the AppMeta component for (help | version | about )
//                val appMeta = AppMeta(about, schema ?: AppBuilder.schema())
//                appMeta.handle(isHelp)
//
//                // Prevent futher processing by return failure
//                Failure(Exception(isHelp.msg), isHelp.status)
//            } else {
//                Success(args)
//            }
        }
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
                else -> println("Unexpected command: " + check.msg)
            }
        }
    }

    private fun help() {
        println("app.area         " + about.area)
        println("app.name         " + about.name)
        println("app.desc         " + about.desc)
        println("app.version      " + about.version)
        println(args.buildHelp())
    }

    private fun about() {
        println("app.area         " + about.area)
        println("app.name         " + about.name)
        println("app.desc         " + about.desc)
        println("app.version      " + about.version)
        println("app.tags         " + about.tags)
        println("app.region       " + about.region)
        println("app.contact      " + about.contact)
        println("app.url          " + about.url)
        println(args.buildHelp())
    }

    private fun version() {
        println(about.version)
    }
}
