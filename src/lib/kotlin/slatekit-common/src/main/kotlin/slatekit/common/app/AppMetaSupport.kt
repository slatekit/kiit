/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.common.app

import slatekit.common.info.Status

interface AppMetaSupport {

    fun appMeta(): AppMeta

    /**
     * builds a list of properties fully describing this app by adding
     * all the properties from the about, host and lang fields.
     *
     * @return
     */
    fun appInfo(addSeparator: Boolean = true): List<Pair<String, Any>> {
        return listOf<Pair<String, Any>>()
    }

    fun appLogStart(callback: (String, String) -> Unit, maxLenField: Int = 0) {
        val meta = appMeta()
        val maxLen = Math.max(maxLenField, "lang.versionNum  ".length)
        callback("name             ".padEnd(maxLen), meta.about.name)
        callback("desc             ".padEnd(maxLen), meta.about.desc)
        callback("version          ".padEnd(maxLen), meta.about.version)
        callback("tags             ".padEnd(maxLen), meta.about.tags)
        callback("group            ".padEnd(maxLen), meta.about.group)
        callback("region           ".padEnd(maxLen), meta.about.region)
        callback("contact          ".padEnd(maxLen), meta.about.contact)
        callback("url              ".padEnd(maxLen), meta.about.url)
        callback("args             ".padEnd(maxLen), meta.start.args.toString())
        callback("env              ".padEnd(maxLen), meta.start.env)
        callback("config           ".padEnd(maxLen), meta.start.config)
        callback("log              ".padEnd(maxLen), meta.start.logFile)
        callback("started          ".padEnd(maxLen), meta.status.started.toString())
        callback("host.name        ".padEnd(maxLen), meta.host.name)
        callback("host.ip          ".padEnd(maxLen), meta.host.ip)
        callback("host.origin      ".padEnd(maxLen), meta.host.origin)
        callback("host.version     ".padEnd(maxLen), meta.host.version)
        callback("lang.name        ".padEnd(maxLen), meta.lang.name)
        callback("lang.version     ".padEnd(maxLen), meta.lang.version)
        callback("lang.versionNum  ".padEnd(maxLen), meta.lang.vendor)
        callback("lang.java        ".padEnd(maxLen), meta.lang.origin)
        callback("lang.home        ".padEnd(maxLen), meta.lang.home)
    }

    fun appLogEnd(
        callback: (String, String) -> Unit,
        status: Status = appMeta().status
    ) {
        val meta = appMeta()
        callback("name             ", meta.about.name)
        callback("desc             ", meta.about.desc)
        callback("version          ", meta.about.version)
        callback("tags             ", meta.about.tags)
        callback("group            ", meta.about.group)
        callback("region           ", meta.about.region)
        callback("contact          ", meta.about.contact)
        callback("url              ", meta.about.url)
        callback("args             ", meta.start.args.toString())
        callback("env              ", meta.start.env)
        callback("config           ", meta.start.config)
        callback("log              ", meta.start.logFile)

        // Commit info
        callback("version          ", meta.build.version)
        callback("commit           ", meta.build.commit)
        callback("date             ", meta.build.date)

        // Status is different at start vs end
        callback("started          ", status.started.toString())
        callback("ended            ", status.ended.toString())
        callback("duration         ", status.duration.toString())
        callback("status           ", status.status)
        callback("errors           ", status.errors.toString())
        callback("error            ", status.error)

        // Host
        callback("host.name        ", meta.host.name)
        callback("host.ip          ", meta.host.ip)
        callback("host.origin      ", meta.host.origin)
        callback("host.version     ", meta.host.version)
        callback("lang.name        ", meta.lang.name)
        callback("lang.version     ", meta.lang.version)
        callback("lang.vendor      ", meta.lang.vendor)
        callback("lang.java        ", meta.lang.origin)
        callback("lang.home        ", meta.lang.home)
    }

    /**
     * iterates over all the items in the metainfo
     *
     * @param callBack
     */
    fun appInfoList(addSeparator: Boolean, callBack: (Int, Pair<String, Any>) -> Unit) {
        // Get all the metadata ( List(( fieldname, value ) )
        val items = appInfo(addSeparator)

        // Find the max metadata property with the max length
        val maxPropLength = items.maxBy { it -> it.first.length }?.first?.length ?: 10

        // Supply each prop/value to caller
        items.forEach { item -> callBack(maxPropLength, item) }
    }
}
