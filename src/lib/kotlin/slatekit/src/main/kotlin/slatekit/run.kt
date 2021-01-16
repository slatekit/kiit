package slatekit

import kotlinx.coroutines.*
import slatekit.app.AppRunner
import slatekit.common.args.Args
import slatekit.common.io.Alias
import slatekit.common.log.LogsDefault
import slatekit.common.writer.ConsoleWriter
import slatekit.context.AppContext
import slatekit.context.Context
import slatekit.generator.Help
import slatekit.results.Failure
import slatekit.results.Success


/**
 * Entry point into the sample console application with support for:
 *
 * 1. environment ( local, dev, qat, pro )
 * 2. command line args
 * 3. argument validation
 * 4. about / help / version display
 * 5. diagnostics ( on startup and end )
 * 6. logging ( console + logback )
 * 7. life-cycle events ( init, exec, end )
 *
 * java -jar ${app.name}.jar ?
 * java -jar ${app.name}.jar --about
 * java -jar ${app.name}.jar --version
 * java -jar ${app.name}.jar -env=dev
 * java -jar ${app.name}.jar -env=dev -log.level=info -conf.dir = "jars"
 * java -jar ${app.name}.jar -env=dev -log.level=info -conf.dir = "conf"
 * java -jar ${app.name}.jar -env=dev -log.level=info -conf.dir = "file://./conf-sample-batch"
 * java -jar ${app.name}.jar -env=dev -log.level=info -conf.dir = "file://./conf-sample-shell"
 * java -jar ${app.name}.jar -env=dev -log.level=info -conf.dir = "file://./conf-sample-server"
 *
 * slatekit new app -name="MyApp1" -packageName="company1.apps"
 * slatekit new api -name="MyAPI1" -packageName="company1.apis"
 * slatekit new cli -name="MyCLI1" -packageName="company1.apps"
 * slatekit new env -name="MyApp2" -packageName="company1.apps"
 * slatekit new job -name="MyJob1" -packageName="company1.jobs"
 * slatekit new lib -name="MyLib1" -packageName="company1.libs"
 * slatekit new orm -name="MyApp1" -packageName="company1.apps"
 *
 * -job.name=queued
 *
 * FUTURE:
 * 1. Support more args: -app.envs="dev,qat,stg,pro" -app.dest="some directory" -sk.version='0.9.28'
 *
 * CODEGEN:
 * 1. slatekit.codegen.toKotlin -templatesFolder="usr://dev/tmp/slatekit/slatekit/scripts/templates/codegen/kotlin" -outputFolder="usr://dev/tmp/codegen/kotlin" -packageName="blendlife"
 *
 * // Test
 * /Users/kishorereddy/git/slatekit/slatekit/src/lib/kotlin/slatekit/build/distributions/slatekit/bin
 */
fun main(args: Array<String>) {
    val parsed = Args.parseArgs(args)
    val help = Help(SlateKit.TITLE)
    val writer = ConsoleWriter()
    when(parsed) {
        is Success -> {
            val parsedArgs = parsed.value
            if(parsedArgs.isHelp) {
                help.show()
            }
            else {
                run(args)
            }
        }
        is Failure -> {
            writer.failure("Error parsing command line arguments")
            help.show()
        }
    }
}

fun run(args:Array<String>){
    /**
     * DOCS : https://www.slatekit.com/arch/app/
     *
     * NOTES: The AppRunner does the following:
     *
     * 1. checks for command line args
     * 2. validates command line args against the Args schema ( optional )
     * 3. builds an AppContext for the app ( containing args, environment, config, logs )
     * 4. creates an App using supplied lambda ( Your Application instance )
     * 5. displays start up information and diagnostics using the Banner
     * 6. executes the life-cycle steps ( init, exec, done )
     */
    runBlocking {
        AppRunner.run(
                cls = SlateKit::class.java,
                rawArgs = args,
                about = SlateKit.about,
                schema = SlateKit.schema,
                enc = SlateKit.encryptor,
                logs = LogsDefault,
                hasAction = true,
                confSource = Alias.Jar,
                builder = { ctx -> SlateKit(version(ctx)) }
        )
    }
}

fun version(ctx:Context): Context {
    return if(ctx is AppContext) {
        ctx.copy(info = ctx.info.copy(build = ctx.info.build.copy(version = "1.34.5")))
    } else ctx
}