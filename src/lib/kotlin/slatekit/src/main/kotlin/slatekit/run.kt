package slatekit

import kotlinx.coroutines.runBlocking
import slatekit.app.AppRunner
import slatekit.common.io.Alias
import slatekit.integration.common.AppEntContext
import slatekit.providers.logs.logback.LogbackLogs


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
 * slatekit new app -name="MyApp1" -packageName="company1.apps" -envs="dev,qat,stg,pro" -dest="some directory" -creds=encrypted -slatekit='0.9.28'
 * slatekit new api -name="MyApp1" -packageName="company1.apps"
 * slatekit new cli -name="MyApp1" -packageName="company1.apps"
 * slatekit new job -name="MyApp1" -packageName="company1.apps"
 * slatekit new orm -name="MyApp1" -packageName="company1.apps"
 */
 fun main(args: Array<String>) {
    runBlocking {
        AppRunner.run(
                rawArgs = args,
                about = SlateKit.about,
                schema = SlateKit.schema,
                enc = SlateKit.encryptor,
                logs = LogbackLogs(),
                hasAction = true,
                confSource = Alias.Cfg,
                builder = { ctx -> SlateKit(ctx) }
        )
    }
}

/*
val samples = listOf(
            "",
            "jar",
            "http://www.slatekit.com:81/apps/app1/env",
            "usr://dev/tmp",
            "tmp://slatekit/apps/app1/env.conf",
            "cfg://slatekit/apps/app1/env.conf",
            "jar://slatekit/apps/app1/env.conf",
            "abs://slatekit/apps/app1/env.conf",
            "http://slatekit.com/apps/app1/env",
            "http://slatekit.com:81/apps/app1/env",
            "http://localhost:9000/apps/app1/env"
            )
    samples.map { slatekit.common.io.Uris.parse(it) }.forEach {
        println("\n")
        println("toString : " + it)
        println("root   : " + it.root.name)
        println("path     : " + it.path)
        if(!it.isEmpty()) {
            println("tofile   : " + it.toFile().absolutePath)
        }
//        println("root   : " + it.root   )
//        println("host     : " + it.host     )
//        println("authority: " + it.authority)
//        println("fragment : " + it.fragment )
//        println("path     : " + it.path     )
//        println("port     : " + it.port     )
    }
 */