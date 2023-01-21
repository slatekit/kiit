package kiit.utils.display

import kiit.utils.writer.ConsoleWriter
import kiit.common.envs.Envs
import kiit.common.info.Info
import kiit.common.log.LogSupport
import kiit.common.log.Logger

open class Banner(
    val info: Info,
    val envs: Envs,
    override val logger: Logger?
) : LogSupport {

    /**
     * Shows the welcome header
     */
    open fun welcome() {
        // Basic welcome
        val writer = ConsoleWriter()
        writer.text("************************************")
        writer.title("Welcome to ${info.about.name}")
        writer.text("************************************")
        writer.line()
        writer.text("starting in environment: " + this.envs.key)
    }

    /**
     * Displays diagnostic info about the app and process
     */
    open fun display() {
        val maxLen = Math.max(0, "lang.versionNum  ".length)
        info("app.area         ".padEnd(maxLen) + info.about.area)
        info("app.name         ".padEnd(maxLen) + info.about.name)
        info("app.desc         ".padEnd(maxLen) + info.about.desc)
        info("app.tags         ".padEnd(maxLen) + info.about.tags)
        info("app.region       ".padEnd(maxLen) + info.about.region)
        info("app.contact      ".padEnd(maxLen) + info.about.contact)
        info("app.url          ".padEnd(maxLen) + info.about.url)
        info("build.version    ".padEnd(maxLen) + info.build.version)
        info("build.commit     ".padEnd(maxLen) + info.build.commit)
        info("build.date       ".padEnd(maxLen) + info.build.date)
        info("host.name        ".padEnd(maxLen) + info.host.name)
        info("host.ip          ".padEnd(maxLen) + info.host.ip)
        info("host.origin      ".padEnd(maxLen) + info.host.origin)
        info("host.version     ".padEnd(maxLen) + info.host.version)
        info("lang.name        ".padEnd(maxLen) + info.lang.name)
        info("lang.version     ".padEnd(maxLen) + info.lang.version)
        info("lang.versionNum  ".padEnd(maxLen) + info.lang.vendor)
        info("lang.java        ".padEnd(maxLen) + info.lang.origin)
        info("lang.home        ".padEnd(maxLen) + info.lang.home)
    }

    /**
     * prints the summary at the end of the application run
     */
    open fun summary() {
        info("===============================================================")
        info("SUMMARY : ")
        info("===============================================================")

        // Standardized info
        // e.g. name, desc, env, log, start-time etc.
        extra().forEach { info(it.first + " = " + it.second) }
        info("===============================================================")
    }

    /**
     * Collection of results executing this application which can be used to display
     * at the end of the application
     */
    open fun extra(): List<Pair<String, String>> {
        return listOf()
    }
}
