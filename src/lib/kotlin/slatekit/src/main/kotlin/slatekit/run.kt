package slatekit

import kotlinx.coroutines.runBlocking
import slatekit.app.AppRunner
import slatekit.common.CommonContext
import slatekit.generator.CredentialMode
import slatekit.docs.DocService
import slatekit.generator.*
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
 */
fun main(args: Array<String>) {
    genSrv(args)
    //app(args)
    //srv(args)
    //cli(args)
    //doc(args)
}


fun cli(args:Array<String>) {
    runBlocking {
        AppRunner.run(
                rawArgs = args,
                about = SlateKit.about,
                schema = SlateKit.schema,
                enc = SlateKit.encryptor,
                logs = LogbackLogs(),
                builder = { ctx -> SlateKit(AppEntContext.fromContext(ctx)) }
        )
    }
}

fun srv(args:Array<String>) {
    runBlocking {
        AppRunner.run(
                rawArgs = args,
                about = slatekit.samples.server.App.about,
                schema = slatekit.samples.server.App.schema,
                enc = slatekit.samples.server.App.encryptor,
                logs = LogbackLogs(),
                builder = { ctx -> slatekit.samples.server.App(AppEntContext.fromContext(ctx)) }
        )
    }
}


fun genApp(args:Array<String>) {
    gen(args, "app8", "Test slate kit app", "myapp.app", "codehelix", "/Users/kishore.reddy/dev/tests/slatekit", Templates.app())
}


fun genLib(args:Array<String>) {
    gen(args,"lib1", "Test slate kit library", "myapp.lib", "codehelix", "/Users/kishore.reddy/dev/tests/slatekit", Templates.lib())
}


fun genSrv(args:Array<String>) {
    gen(args,"srv2", "Test slate kit server", "myapp.srv", "codehelix", "/Users/kishore.reddy/dev/tests/slatekit", Templates.srv())
}


fun gen(args:Array<String>, name:String, desc:String, packageName:String, company:String, dest:String, template: Template) {
    val svc = GeneratorService(CommonContext.simple(""), SlateKit::class.java)
    val ctx = GeneratorContext(name, desc, packageName, company, dest, CredentialMode.EnvVars)
    svc.generate(ctx, template)
}


fun doc(args:Array<String>){

    val root = "/Users/kishorereddy/git/slatekit"
    val svc = DocService(
            "$root/slatekit",
            "$root/slatekit-site/src/hugo/slatekit-v3/content/core",
            "scripts/doc/doc_template_cloud.md")
    svc.processItems(listOf(
            "AWS-S3",
            "AWS-SQS"
    ))
}