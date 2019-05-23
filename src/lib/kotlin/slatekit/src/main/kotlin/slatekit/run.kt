package slatekit


import kotlinx.coroutines.runBlocking
import slatekit.app.AppRunner
import slatekit.common.CommonContext
import slatekit.docs.DocService
import slatekit.generator.CredentialMode
import slatekit.integration.common.AppEntContext
import slatekit.providers.logs.logback.LogbackLogs
import slatekit.generator.GeneratorContext
import slatekit.generator.GeneratorService
import slatekit.generator.Templates


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
    //cli(args)
    app(args)
}



fun cli(args: Array<String>) {
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


fun app(args:Array<String>) {
    //    val text = File(url.file).readText()
    val svc = GeneratorService(CommonContext.simple(""))
    val ctx = GeneratorContext("app6", "Test slate kit", "codehelix.app2", "codehelix", "/Users/kishore.reddy/dev/tests/slatekit", CredentialMode.EnvVars)
    svc.generate(ctx, Templates.app())
}


//fun test(){
//
//    val root = "/Users/kishorereddy/git/slatekit"
//    val svc = DocService(
//            "$root/slatekit",
//            "$root/slatekit-site/src/hugo/slatekit-v3/content/core",
//            "scripts/doc/doc_template_cloud.md")
//    svc.processItems(listOf(
//            "AWS-S3",
//            "AWS-SQS"
////            "Ctx",
////            "Cmd",
////            "Email",
////            "Sms"
//    ))
//
//    //println("slatekit command line")
//    //run2(args)
//}