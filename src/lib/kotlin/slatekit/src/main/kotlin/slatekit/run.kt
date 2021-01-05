package slatekit

import kotlinx.coroutines.*
import slatekit.app.AppBuilder
import slatekit.app.AppRunner
import slatekit.app.AppUtils
import slatekit.common.DateTime
import slatekit.common.args.Args
import slatekit.common.conf.*
import slatekit.common.display.Banner
import slatekit.common.envs.Envs
import slatekit.common.info.*
import slatekit.common.io.Alias
import slatekit.common.io.Uri
import slatekit.common.log.LoggerConsole
import slatekit.common.log.LogsDefault
import slatekit.context.AppContext
import slatekit.results.Failure
import slatekit.results.Success
import slatekit.results.Try
import slatekit.results.builders.Tries
import java.io.FileInputStream
import java.util.*


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
 * slatekit new app -name="MyApp1" -package="company1.apps"
 * slatekit new api -name="MyAPI1" -package="company1.apis"
 * slatekit new cli -name="MyCLI1" -package="company1.apps"
 * slatekit new env -name="MyApp2" -package="company1.apps"
 * slatekit new job -name="MyJob1" -package="company1.jobs"
 * slatekit new lib -name="MyLib1" -package="company1.libs"
 * slatekit new orm -name="MyApp1" -package="company1.apps"
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
    //app(args)
    val about = SlateKit.about
    SlateKit.log(about, LoggerConsole())
    val folders = Folders.userDir(SlateKit.about)
    folders.create()
    val settings = PropSettings(dir = folders.pathToConf, name = "settings.conf")
    settings.putString("slatekit.version", "1.28.0")
    settings.putString("slatekit.version.beta", "0.58")
    settings.putString("kotlin.version", "1.3.21")
    settings.putString("generation.source",  "usr://slatekit/generator")
    settings.putString("generation.templates",  "usr://slatekit/generator/templates")
    settings.putString("generation.output", "usr://slatekit/generator/gen")
    settings.save(true, "Default settings")
}

fun app(args: Array<String>) {
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
                builder = { ctx -> SlateKit(ctx) }
        )
    }
}


fun cli(args: Array<String>) {
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
                builder = { ctx -> SlateKit(ctx) }
        )
    }
}


//class CacheTests {
//    suspend fun cache() {
//        val cache = SimpleAsyncCache.of("test")
//        val scope = CoroutineScope(Dispatchers.IO)
//        scope.launch {
//            cache.work()
//        }
//        cache.put("a", "", 200) { delay(2000); 1 }
//        cache.put("b", "", 200) { delay(2000); 2 }
//        cache.put("c", "", 200) { delay(2000); 3 }
//        val items = listOf("a", "b", "c")
//
//        for(ndx in 1..100) {
//            scope.launch {
//                delay(1000)
//                val time = DateTime.now()
//                val r = Random.nextInt(0, 3)
//                val key = items[r]
//                val value = cache.get<Int>(key)
//                val name = this.toString()
//                println("$time : $name : $value")
//            }
//        }
//        delay(12000)
//        println("done")
//    }
//}