package slatekit


import slatekit.app.AppRunner
import slatekit.providers.logs.logback.LogbackLogs


/**
 * Entry point into the sample console application.
 */
fun main(args: Array<String>) {
    AppRunner.run(
            rawArgs = args,
            about = SlateKit.about,
            schema = SlateKit.schema,
            enc = SlateKit.encryptor,
            logs = LogbackLogs(),
            builder = { ctx -> SlateKit(ctx) }
    )
}