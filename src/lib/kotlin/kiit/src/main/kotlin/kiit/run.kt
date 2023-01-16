package kiit

import kotlinx.coroutines.*
import kiit.app.AppRunner
import kiit.common.args.Args
import kiit.common.io.Alias
import kiit.utils.writer.ConsoleWriter
import kiit.context.AppContext
import slatekit.generator.Help
import kiit.providers.logback.LogbackLogs
import kiit.results.Failure
import kiit.results.Success


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
    val help = Help(Kiit.TITLE)
    val writer = ConsoleWriter()
    when(parsed) {
        is Success -> {
            val parsedArgs = parsed.value
            if(parsedArgs.isHelp) {
                help.show()
            }
            else {
                run(args)
                //api(args)
            }
        }
        is Failure -> {
            writer.failure("Error parsing command line arguments")
            help.show()
        }
    }
}


fun api(args: Array<String>) {
    val ctx = AppContext.simple(Server::class.java, "test")
    runBlocking {
        val srv = Server(ctx)
        srv.execute()
    }
}


fun run(args:Array<String>){
    /**
     * DOCS : https://www.kiit.dev/arch/app/
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
                cls = Kiit::class.java,
                rawArgs = args,
                about = Kiit.about,
                schema = Kiit.schema,
                enc = Kiit.encryptor,
                logs = LogbackLogs(),
                hasAction = true,
                source = Alias.Jar,
                builder = { ctx -> Kiit(ctx) }
        )
    }
}