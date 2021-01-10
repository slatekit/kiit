package slatekit.generator

import slatekit.SlateKit
import slatekit.common.Settings
import slatekit.common.conf.Config
import slatekit.common.conf.PropSettings
import slatekit.common.conf.Props
import slatekit.common.info.Folders
import slatekit.context.Context
import java.io.File

class Setup(val ctx: Context) {

    suspend fun config(): Config {
        // Current version
        val slatekitVersion = ctx.conf.getString("slatekit.version")
        val brewAppLocation = "/usr/local/Cellar/slatekit/${slatekitVersion}"

        // Create the root folder using about info {COMPANY}/{AREA}/{NAME}
        // eg. ~/slatekit/tools/cli
        val folders = Folders.userDir(SlateKit.about)
        folders.create()
        val pathToHOME = folders.pathToApp

        // Load settings
        // NOTES:
        // 1. Home location is where the settings, logs are maintained ( regardless of version )
        // 2. Install location is baesd on env variable or homebrew ( supported ), this changes based on version
        val homeLocation = System.getenv("SLATEKIT_TOOLS_CLI_HOME") ?:pathToHOME
        val installLocation = System.getenv("SLATEKIT_TOOLS_CLI_HOME") ?: brewAppLocation
        val settingsName = "settings.conf"
        val confDir = File(homeLocation, folders.conf)
        val file = File(confDir, settingsName)
        if(!file.exists()) {
            val settings = PropSettings(dir = confDir.absolutePath, name = settingsName)
            settings.put("slatekit.version", slatekitVersion, false)
            settings.put("slatekit.version.beta", ctx.conf.getString("slatekit.version.beta"), false)
            settings.put("kotlin.version", ctx.conf.getString("kotlin.version"), false)
            settings.put("generation.source", "$installLocation/templates", false)
            settings.put("generation.output", "", false)
            settings.save(desc = "default settings")
        }
        else {
            val settings = Props.fromFile(file.absolutePath)
            settings.put("generation.source", "$installLocation/templates")
            PropSettings.save(settings, confDir.absolutePath, settingsName, true, "upgrade to $slatekitVersion")
        }
        // Now load from HOME/conf/settings.conf
        val settings = Config.of(SlateKit::class.java, file.absolutePath)
        return settings
    }
}