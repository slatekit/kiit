package slatekit


import slatekit.app.AppRunner
import slatekit.docs.DocService
import slatekit.integration.common.AppEntContext
import slatekit.providers.logs.logback.LogbackLogs


/**
 * Entry point into the sample console application.
 */
fun main(args: Array<String>) {

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


fun run2(args: Array<String>) {
    AppRunner.run(
            rawArgs = args,
            about = SlateKit.about,
            schema = SlateKit.schema,
            enc = SlateKit.encryptor,
            logs = LogbackLogs(),
            builder = { ctx -> SlateKit(AppEntContext.fromContext(ctx)) }
    )
}