package slatekit.generator

import slatekit.common.conf.Conf
import slatekit.common.conf.Config
import slatekit.common.conf.PropSettings
import slatekit.common.conf.Props
import slatekit.common.info.About
import slatekit.common.info.Folders
import slatekit.context.Context
import java.io.File

class Setup(val cls:Class<*>, val ctx: Context) {

    /**
     * Creates / Updates the Slate Kit home directory and settings ~/.slatekit/tools/cli/conf/settings.conf
     * 1. Updates the current version number
     * 2. Updates the path to the templates of the slate kit version installed/upgraded to
     */
    suspend fun configure(): Config {
        // Create the app dir ~/.slatekit/tools/cli ( conf, output, etc )
        ctx.dirs?.create()

        // Current version
        val slatekitVersionCli = ctx.conf.getString(KEY_SLATEKIT_VERSION_CLI)
        val info = SetupInfo(slatekitVersionCli, ctx.info.about)

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
        val settings = Config.of(cls, file.absolutePath)
        return settings
    }


    /**
     * Creates the slatekit home directory and settings
     * settings: ~/.slatekit/tools/cli/conf/settings.conf
     */
    private fun create(conf: Conf, info: SetupInfo) {
        val settings = PropSettings(dir = info.confDir.absolutePath, name = info.settingsName)
        settings.put(KEY_SLATEKIT_VERSION, conf.getString(KEY_SLATEKIT_VERSION), false)
        settings.put(KEY_SLATEKIT_VERSION_BETA, conf.getString(KEY_SLATEKIT_VERSION_BETA), false)
        settings.put(KEY_KOTLIN_VERSION, conf.getString(KEY_KOTLIN_VERSION), false)
        settings.put(KEY_GENERATION_SOURCE, "${info.installLocation}/templates", false)
        settings.put(KEY_GENERATION_OUTPUT, "", false)
        settings.save(desc = "default settings")
    }


    /**
     * Updates the settings to path to templates of slate kit LATEST version
     * settings: ~/.slatekit/tools/cli/conf/settings.conf
     */
    private fun update(conf: Conf, info: SetupInfo){
        val file = File(info.confDir, info.settingsName)
        val raw = Props.fromFile(file.absolutePath)
        val settings = PropSettings(raw, dir = info.confDir.absolutePath, name = info.settingsName)

        // Only update if different version
        val current = settings.getString(KEY_SLATEKIT_VERSION)
        if(current != info.slatekitVersion) {
            settings.put(KEY_SLATEKIT_VERSION, conf.getString(KEY_SLATEKIT_VERSION))
            settings.put(KEY_SLATEKIT_VERSION_BETA, conf.getString(KEY_SLATEKIT_VERSION_BETA))
            settings.put(KEY_KOTLIN_VERSION, conf.getString(KEY_KOTLIN_VERSION))
            settings.put(KEY_GENERATION_SOURCE, "${info.installLocation}/templates")
            settings.save(desc = "upgrade to ${info.slatekitVersion}")
        }
    }


    data class SetupInfo(val slatekitVersion:String, val about: About) {

        // For MAC
        val brewAppLocation = "/usr/local/Cellar/slatekit/${slatekitVersion}"

        // Build folder structure from {company}/{area}/{name}
        val folders = Folders.userDir(about)

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


    companion object {
        const val KEY_KOTLIN_VERSION = "kotlin.version"
        const val KEY_SLATEKIT_VERSION = "slatekit.version"
        const val KEY_SLATEKIT_VERSION_BETA = "slatekit.version.beta"
        const val KEY_SLATEKIT_VERSION_CLI = "slatekit.version.cli"
        const val KEY_GENERATION_SOURCE = "generation.source"
        const val KEY_GENERATION_OUTPUT = "generation.output"
    }
}