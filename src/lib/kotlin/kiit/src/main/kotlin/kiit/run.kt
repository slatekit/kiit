package kiit

import kotlinx.coroutines.*
import kiit.app.AppRunner
import kiit.common.DateTime
import kiit.common.args.Args
import kiit.common.conf.Conf
import kiit.common.conf.Config
import kiit.common.data.*
import kiit.common.io.Alias
import kiit.utils.writer.ConsoleWriter
import kiit.context.AppContext
import kiit.db.Db
import kiit.generator.Help
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
 * kiit new app -name="MyApp1" -packageName="company1.apps"
 * kiit new api -name="MyAPI1" -packageName="company1.apis"
 * kiit new cli -name="MyCLI1" -packageName="company1.apps"
 * kiit new env -name="MyApp2" -packageName="company1.apps"
 * kiit new job -name="MyJob1" -packageName="company1.jobs"
 * kiit new lib -name="MyLib1" -packageName="company1.libs"
 * kiit new orm -name="MyApp1" -packageName="company1.apps"
 *
 * -job.name=queued
 *
 * FUTURE:
 * 1. Support more args: -app.envs="dev,qat,stg,pro" -app.dest="some directory" -sk.version='0.9.28'
 *
 * CODEGEN:
 * 1. kiit.codegen.toKotlin -templatesFolder="usr://dev/tmp/slatekit/slatekit/scripts/templates/codegen/kotlin" -outputFolder="usr://dev/tmp/codegen/kotlin" -packageName="myapp"
 *
 * // Test
 * /Users/kishorereddy/git/slatekit/slatekit/src/lib/kotlin/slatekit/build/distributions/slatekit/bin
 */

data class DbVersion(val version:Int, val dated:String, val name:String, val details:String,
                     val status:String, val code:Int, val message:String?,
                     val label:String?, val tags:String?, val createdAt:DateTime, val createdBy:String)


fun main(args: Array<String>) {
    runBlocking {
    }
//    //test(args)
//    val tester = Tester1()
//    tester.run()
}

/**
 * Simulates some non-blocking/async operation like an API call
 */
suspend fun task(name:String, delay:Long) {
    println("starting name=$name, thread=${Thread.currentThread().name}")
    delay(delay)
    println("finished name=$name, thread=${Thread.currentThread().name}")
}


fun test_db() {
    val url = System.getenv("APP_DB_URL")
    val user = System.getenv("APP_DB_USER")
    val pass = System.getenv("APP_DB_PASS")
    val dbcon = DbConString(vendor = Vendor.MySql, url = url, user = user, pswd = pass)
    val db = Db(dbcon)
    val version = db.mapOne("select * from `version` order by id desc limit 1;", null) { rec ->
        DbVersion(
            version = rec.getInt("version"),
            dated = rec.getString("dated"),
            name = rec.getString("name"),
            details = rec.getString("details"),
            status = rec.getString("status"),
            code = rec.getInt("code"),
            message = rec.getStringOrNull("message"),
            label = rec.getStringOrNull("label"),
            tags = rec.getStringOrNull("tags"),
            createdAt = rec.getDateTime("createdAt"),
            createdBy = rec.getString("createdBy")
        )
    }
    println(version)
}

fun conf(env:String = "qat"):Conf {
    val basepath = "usr://dev/myapp/myapp-server/src/server/kotlin/myapp-server/src/main/resources/env.conf"
    val envpath  = "usr://dev/myapp/myapp-server/src/server/kotlin/myapp-server/src/main/resources/env.${env}.conf"

    val conf = Config.of(Server::class.java, envpath, basepath, null) //, basepath, null)
    return conf
}

fun test(args:Array<String>) {
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
                //run(args)
                api(args)
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


fun run(args:Array<String>) {
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