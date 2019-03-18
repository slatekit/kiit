package slatekit


import slatekit.app.AppRunner
import slatekit.docs.DocService
import slatekit.integration.common.AppEntContext
import slatekit.providers.logs.logback.LogbackLogs


/**
 * Entry point into the sample console application.
 */
fun main(args: Array<String>) {

    val svc = DocService(
            "/Users/kishore.reddy/dev/tmp/slatekit/slatekit",
            "/Users/kishore.reddy/dev/tmp/slatekit/slatekit-site/src/hugo/slatekit-v3/content/utils",
            "scripts/doc/doc_template_kotlin.md")
    svc.processProject("slatekit-common")

}


fun run(args:Array<String>) {
    AppRunner.run(
            rawArgs = args,
            about = SlateKit.about,
            schema = SlateKit.schema,
            enc = SlateKit.encryptor,
            logs = LogbackLogs(),
            builder = { ctx -> SlateKit(AppEntContext.fromContext(ctx)) }
    )
}