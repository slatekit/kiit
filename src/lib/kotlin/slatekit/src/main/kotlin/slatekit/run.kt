package slatekit

import kotlinx.coroutines.*
import slatekit.app.AppBuilder
import slatekit.app.AppRunner
import slatekit.app.AppUtils
import slatekit.common.DateTime
import slatekit.common.args.Args
import slatekit.common.conf.Conf
import slatekit.common.conf.Confs
import slatekit.common.conf.Config
import slatekit.common.conf.Props
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

    Tries.of {
        val envs = Envs.defaults("dev")
        val info = Info(
                About.simple("slatekit", "cli", "", ""),
                Build("1.38.0", "abc", "main", "12-30-20"),
                Host.local(),
                Lang.kotlin()
        )
        val logger = LoggerConsole()
        val banner = Banner(info, envs, logger)
        banner.welcome()
        banner.display()
        val confTest = Tries.of { conf(SlateKit::class.java, args) }
        val jar1Test = Tries.of { Props2.loadFromJar(SlateKit::class.java,"env.conf") }
        val jar2Test = Tries.of { Props2.loadFromJar2(SlateKit::class.java,"env.conf") }
        val jar3Test = Tries.of { loadFromJar3("env.conf") }
        test(confTest, "auto")
        test(jar1Test, "jar1")
        test(jar2Test, "jar2")
        test(jar3Test, "jar3")
        println()
        logger.info("user.dir : " + System.getProperty("user.dir"))
        logger.info("path.get : " + java.nio.file.Paths.get("").toAbsolutePath().toString())
    }
}

fun test(res:Try<Properties>, desc:String){
    println()
    println("======================================")
    when(res){
        is Success -> println("conf.${desc} : " + res.value.getProperty("slatekit.title"))
        is Failure -> println("conf.${desc} : FAILED error=${res.error.message}")
    }
}


/**
 * Problems:
 * 1. Use SlateKit::class not Props { ... this.class }
 * 2. Use cls.getResourceAsStream("/" + path) instead of getResource(..).file
 * 3. Missing pattern match in loadFrom { ... is Alias.Jar   -> loadFromJar2(cls,uri.path ?: Confs.CONFIG_DEFAULT_PROPERTIES) }
 * 4. Uri.parse( for jar is buggy ) ? maybe not even have a value
 * 5. Need separate conf function to load conf like below
 * 6. Maybe add Parent Alias PRN
 * 7. Support -conf.dir=PRN://conf, -conf.dir=../conf = other ( but figure out path )
 */
fun conf(cls:Class<*>, raw: Array<String>, name:String = Confs.CONFIG_DEFAULT_PROPERTIES, alias:Alias = Alias.Jar): Properties {
    val args = Args.parseArgs(raw).getOrNull() ?: Args.empty()
    val source = AppBuilder.dir(args, alias)
    val path = source.combine(name)
    println("CFG name=${name}")
    println("CFG alias=${alias.name}")
    source.print()
    val props = Props2.loadFrom(cls, path)
    return props
}


fun Uri.print() {
    println()
    println("==============================================")
    println("URI full=${this.full}")
    println("URI path=${this.path}")
    println("URI raw=${this.raw}"  )
    println("URI root=${this.root}")
    println("==============================================")
    println()
}


object Props2 {
    fun loadFrom(cls:Class<*>, uri: Uri): Properties {
        uri.print()
        val props = when (uri.root) {
            null -> loadFromJar2(cls,uri.path ?: Confs.CONFIG_DEFAULT_PROPERTIES)
            is Alias.Jar   -> loadFromJar2(cls,uri.path ?: Confs.CONFIG_DEFAULT_PROPERTIES)
            is Alias.Other -> loadFromJar2(cls,uri.path ?: Confs.CONFIG_DEFAULT_PROPERTIES)
            else -> loadFromPath(uri.toFile().absolutePath)
        }
        return props
    }


    fun loadFromJar(cls:Class<*>, path: String): Properties {
        // This is here to debug loading app conf
        val file = cls.getResource("/" + path).file
        val input = FileInputStream(file)
        val conf = Properties()
        conf.load(input)
        return conf
    }


    fun loadFromJar2(cls:Class<*>, path: String): Properties {
        // This is here to debug loading app conf
        val input = cls.getResourceAsStream("/" + path)
        val conf = Properties()
        conf.load(input)
        return conf
    }

    fun loadFromPath(path: String): Properties {
        // This is here to debug loading app conf
        val input = FileInputStream(path)
        val conf = Properties()
        conf.load(input)
        return conf
    }
}

fun loadFromJar3(path: String): Properties {
    // This is here to debug loading app conf
    println("Load jar path =/$path")
    val clsK = SlateKit::class
    val clsJ = clsK.java
    val input = clsJ.getResourceAsStream("/" + path)
    val conf = Properties()
    conf.load(input)
    return conf
}

fun app(args:Array<String>) {
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
                app = SlateKit::class.java,
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


fun cli(args:Array<String>) {
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