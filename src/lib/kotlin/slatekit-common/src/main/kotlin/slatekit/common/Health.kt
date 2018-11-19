package slatekit.common

import slatekit.common.info.Info

class Health(val ctx:Context) {

    fun heartbeat():Group<Pair<String,String>> {
        val info = version()
        val enriched = info.items.plus("timestamp" to DateTime.now().toStringUtc())
        return info.copy(items = enriched)
    }


    fun version():Group<Pair<String,String>> {
        return Group("version", "health", ctx.build.props())
    }


    fun check():Group<Pair<String,String>> {
        return Group("check", "health", listOf())
    }


    fun info():List<Group<Pair<String,String>>> {
        val parts = listOf(
                "build" to ctx.app.build,
                "host" to ctx.app.host,
                "lang" to ctx.app.lang,
                "start" to ctx.app.start
        )

        val all = parts.map { Group(it.first, "health", it.second.props()) }
        return all
    }


    fun collect():Group<Pair<String,String>> {
        val parts = listOf<Info>(ctx.app.build, ctx.app.host, ctx.app.lang, ctx.app.start)
        val names = listOf("build", "host", "lang", "start").joinToString(",")
        val collected = parts.map { it.props() }.flatten()
        return Group(names, "health", collected)
    }


    fun detail():Group<Pair<String,String>> {
        return Group("infra", "health", ctx.build.props())
    }
}