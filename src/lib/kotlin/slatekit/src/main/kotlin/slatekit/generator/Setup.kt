package slatekit.generator

import slatekit.SlateKit
import slatekit.common.conf.Conf
import slatekit.common.conf.Config
import slatekit.common.conf.PropSettings
import slatekit.common.conf.Props
import slatekit.common.info.Folders
import slatekit.context.Context
import java.io.File

class Setup(val ctx: Context) {

    /**
     * Creates / Updates the Slate Kit home directory and settings ~/.slatekit/tools/cli/conf/settings.conf
     * 1. Updates the current version number
     * 2. Updates the path to the templates of the slate kit version installed/upgraded to
     */
    suspend fun install(): Config {
        // Current version
        val slatekitVersion = slatekitVersion(ctx.conf)
        val info = SetupInfo(slatekitVersion)

        // Check for ~/.slatekit/tools/cli/conf/settings.conf
        val file = File(info.confDir, info.settingsName)
        val conf = ctx.conf

        // 1st time install
        if(!file.exists()) {
            create(conf, info)
        }
        // 2nd time upgrade
        else {
            update(conf, info)
        }
        // Now load from HOME/conf/settings.conf
        val settings = Config.of(SlateKit::class.java, file.absolutePath)
        return settings
    }


    /**
     * Creates the slatekit home directory and settings
     * settings: ~/.slatekit/tools/cli/conf/settings.conf
     */
    private fun create(conf: Conf, info:SetupInfo) {
        val settings = PropSettings(dir = info.confDir.absolutePath, name = info.settingsName)
        settings.put("slatekit.version", info.slatekitVersion, false)
        settings.put("slatekit.version.beta", conf.getString("slatekit.version.beta"), false)
        settings.put("kotlin.version", conf.getString("kotlin.version"), false)
        settings.put("generation.source", "${info.installLocation}/templates", false)
        settings.put("generation.output", "", false)
        settings.save(desc = "default settings")
    }


    /**
     * Updates the settings to path to templates of slate kit LATEST version
     * settings: ~/.slatekit/tools/cli/conf/settings.conf
     */
    private fun update(conf: Conf, info:SetupInfo){
        val file = File(info.confDir, info.settingsName)
        val raw = Props.fromFile(file.absolutePath)
        val settings = PropSettings(raw, dir = info.confDir.absolutePath, name = info.settingsName)
        settings.put("slatekit.version", info.slatekitVersion)
        settings.put("slatekit.version.beta", conf.getString("slatekit.version.beta"))
        settings.put("kotlin.version", conf.getString("kotlin.version"))
        settings.put("generation.source", "${info.installLocation}/templates")
        settings.save(desc = "upgrade to ${info.slatekitVersion}")
    }


    private fun slatekitVersion(config: Conf): String = ctx.conf.getString("slatekit.version")


    data class SetupInfo(val slatekitVersion:String) {

        // For MAC
        val brewAppLocation = "/usr/local/Cellar/slatekit/${slatekitVersion}"

        // Build folder structure from {company}/{area}/{name}
        val folders = Folders.userDir(SlateKit.about)

        // ~/.slatekit/tools/cli
        val pathToHOME = folders.pathToApp

        // Allow override for HOME DIR
        val homeLocation = System.getenv("SLATEKIT_TOOLS_CLI_HOME") ?:pathToHOME

        // Allow override for INSTALL DIR ( for non *nix )
        val installLocation = System.getenv("SLATEKIT_TOOLS_CLI_HOME") ?: brewAppLocation

        // Settings file ~/.slatekit/tools/cli/conf/settings.conf
        val settingsName = "settings.conf"

        // ~/.slatekit/tools/cli/conf
        val confDir = File(homeLocation, folders.conf)
    }
}