package slatekit


import kotlinx.coroutines.runBlocking
import slatekit.app.AppRunner
import slatekit.common.CommonContext
import slatekit.docs.DocService
import slatekit.integration.common.AppEntContext
import slatekit.providers.logs.logback.LogbackLogs
import slatekit.setup.SetupContext
import slatekit.setup.SetupService
import java.io.File


/**
 * Entry point into the sample console application.
 */
fun main(args: Array<String>) {
//    val url = SlateKit::class.java.getResource("/templates/app/build.txt")
//    val text = File(url.file).readText()
    val svc = SetupService(CommonContext.simple(""))
    val ctx = SetupContext("app1", "Test slate kit", "codehelix.app1", "~/dev/tests/slatekit")
    svc.app(ctx)
    println("done")
}


fun test2(args:Array<String>) {
//    val url = SlateKit::class.java.getResource("/templates/app/App.txt")
//    val text = File(url.file).readText()
//    println(text)

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


fun test(){

    val root = "/Users/kishorereddy/git/slatekit"
    val svc = DocService(
            "$root/slatekit",
            "$root/slatekit-site/src/hugo/slatekit-v3/content/core",
            "scripts/doc/doc_template_cloud.md")
    svc.processItems(listOf(
            "AWS-S3",
            "AWS-SQS"
//            "Ctx",
//            "Cmd",
//            "Email",
//            "Sms"
    ))

    //println("slatekit command line")
    //run2(args)
}