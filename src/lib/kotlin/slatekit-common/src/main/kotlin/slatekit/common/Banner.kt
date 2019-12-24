package slatekit.common

import slatekit.common.console.SemanticConsole
import slatekit.common.log.LogSupport
import slatekit.common.log.Logger

open class Banner(val ctx: Context,
                  override val logger: Logger?,
                  val showWelcome: Boolean = true,
                  val showDisplay: Boolean = true,
                  val showGoodbye: Boolean = true) : LogSupport {

    /**
     * Shows the welcome header
     */
    open fun welcome() {
        // Basic welcome
        if(showWelcome) {
            val writer = SemanticConsole()
            writer.text("************************************")
            writer.title("Welcome to ${ctx.info.about.name}")
            writer.text("************************************")
            writer.line()
            writer.text("starting in environment: " + this.ctx.envs.key)
        }
    }

    /**
     * Displays diagnostic info about the app and process
     */
    open fun display() {
        if(showDisplay) {
            val maxLen = Math.max(0, "lang.versionNum  ".length)
            info("app.area         ".padEnd(maxLen) + ctx.info.about.area)
            info("app.name         ".padEnd(maxLen) + ctx.info.about.name)
            info("app.desc         ".padEnd(maxLen) + ctx.info.about.desc)
            info("app.version      ".padEnd(maxLen) + ctx.info.about.version)
            info("app.tags         ".padEnd(maxLen) + ctx.info.about.tags)
            info("app.region       ".padEnd(maxLen) + ctx.info.about.region)
            info("app.contact      ".padEnd(maxLen) + ctx.info.about.contact)
            info("app.url          ".padEnd(maxLen) + ctx.info.about.url)
            info("build.version    ".padEnd(maxLen) + ctx.info.build.version)
            info("build.commit     ".padEnd(maxLen) + ctx.info.build.commit)
            info("build.date       ".padEnd(maxLen) + ctx.info.build.date)
            info("host.name        ".padEnd(maxLen) + ctx.info.system.host.name)
            info("host.ip          ".padEnd(maxLen) + ctx.info.system.host.ip)
            info("host.origin      ".padEnd(maxLen) + ctx.info.system.host.origin)
            info("host.version     ".padEnd(maxLen) + ctx.info.system.host.version)
            info("lang.name        ".padEnd(maxLen) + ctx.info.system.lang.name)
            info("lang.version     ".padEnd(maxLen) + ctx.info.system.lang.version)
            info("lang.versionNum  ".padEnd(maxLen) + ctx.info.system.lang.vendor)
            info("lang.java        ".padEnd(maxLen) + ctx.info.system.lang.origin)
            info("lang.home        ".padEnd(maxLen) + ctx.info.system.lang.home)
        }
    }

    /**
     * prints the summary at the end of the application run
     */
    open fun summary() {
        if(showGoodbye) {
            info("===============================================================")
            info("SUMMARY : ")
            info("===============================================================")

            // Standardized info
            // e.g. name, desc, env, log, start-time etc.
            extra().forEach { info(it.first + " = " + it.second) }
            info("===============================================================")
        }
    }

    /**
     * Collection of results executing this application which can be used to display
     * at the end of the application
     */
    open fun extra(): List<Pair<String, String>> {
        return listOf()
    }
}