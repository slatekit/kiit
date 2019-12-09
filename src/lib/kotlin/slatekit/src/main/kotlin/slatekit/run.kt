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
 * slatekit new app -name="MyApp2" -package="company1.apps"
 * slatekit new app -name="MyApp1" -package="company1.apps"
 * slatekit new api -name="MyApp1" -package="company1.apps"
 * slatekit new cli -name="MyApp1" -package="company1.apps"
 * slatekit new job -name="MyApp1" -package="company1.apps"
 * slatekit new lib -name="MyApp1" -package="company1.apps"
 * slatekit new orm -name="MyApp1" -package="company1.apps"
 *
 * FUTURE:
 * 1. Support more args: -app.envs="dev,qat,stg,pro" -app.dest="some directory" -sk.version='0.9.28'
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