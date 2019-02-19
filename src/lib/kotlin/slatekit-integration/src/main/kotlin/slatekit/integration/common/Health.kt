package slatekit.integration.common

import slatekit.common.Context
import slatekit.common.DateTime
import slatekit.common.diagnostics.Check
import slatekit.common.diagnostics.Status
import slatekit.common.info.Meta

open class Health(val ctx: Context) {

    private val checks = listOf(
            Check("app.data" , "n/a", "db"    , "rds"     , .001),
            Check("app.stor" , "n/a", "files" , "s3"      , .001),
            Check("app.work" , "n/a", "queue" , "sqs"     , .001),
            Check("app.sms"  , "n/a", "sms"   , "twilio"  , .001),
            Check("app.email", "n/a", "emails", "sendgrid", .001)
    )

    fun heartbeat(): Group<Pair<String, String>> {
        val info = version()
        val enriched = info.items.plus("timestamp" to DateTime.now().toStringUtc())
        return info.copy(items = enriched)
    }


    fun version(): Group<Pair<String, String>> {
        return Group("version", "health", ctx.app.build.props())
    }


    open fun check():Boolean {
        // Your health check logic here
        return true
    }


    open fun components(): Group<Status> {
        return Group("check", "health", checks.map { it.status() })
    }


    fun info():List<Group<Pair<String, String>>> {
        val parts = listOf(
                "build" to ctx.app.build,
                "host" to ctx.app.host,
                "lang" to ctx.app.lang,
                "start" to ctx.app.start
        )

        val all = parts.map { Group(it.first, "health", it.second.props()) }
        return all
    }


    fun collect(): Group<Pair<String, String>> {
        val parts = listOf<Meta>(ctx.app.build, ctx.app.host, ctx.app.lang, ctx.app.start)
        val names = listOf("build", "host", "lang", "start").joinToString(",")
        val collected = parts.map { it.props() }.flatten()
        return Group(names, "health", collected)
    }


    fun detail(): Group<Pair<String, String>> {
        return Group("infra", "health", ctx.app.build.props())
    }
}