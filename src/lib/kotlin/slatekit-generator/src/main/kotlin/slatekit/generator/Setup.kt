package slatekit.generator

import kiit.common.conf.Conf
import kiit.common.conf.Config
import kiit.common.conf.PropSettings
import kiit.common.conf.Props
import kiit.common.info.About
import kiit.common.info.Folders
import kiit.utils.writer.ConsoleWriter
import kiit.context.Context
import java.io.File

class Setup(val cls:Class<*>, val ctx: Context) {
    private val writer = ConsoleWriter()

    /**
     * Creates / Updates the Slate Kit home directory and settings ~/.kiit/tools/cli/conf/settings.conf
     * 1. Updates the current version number
     * 2. Updates the path to the templates of the slate kit version installed/upgraded to
     */
    suspend fun configure(): Config {
        // Create the app dir ~/.slatekit/tools/cli ( conf, output, etc )
        ctx.dirs?.create()

        // Current version
        val slatekitVersionCli = ctx.conf.getString(KEY_KIIT_VERSION_CLI)
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
        writer.text("")
        
        // Now load from HOME/conf/settings.conf
        val settings = Config.of(cls, file.absolutePath)
        return settings
    }


    /**
     * Creates the kiit home directory and settings
     * settings: ~/.kiit/tools/cli/conf/settings.conf
     */
    private fun create(conf: Conf, info: SetupInfo) {
        writer.highlight("Creating settings at ~/.kiit/tools/cli/conf/settings.conf to ${info.slatekitVersion}")
        val settings = PropSettings(dir = info.confDir.absolutePath, name = info.settingsName)
        settings.putString(KEY_KIIT_VERSION, conf.getString(KEY_KIIT_VERSION))
        settings.putString(KEY_KIIT_VERSION_BETA, conf.getString(KEY_KIIT_VERSION_BETA))
        settings.putString(KEY_KOTLIN_VERSION, conf.getString(KEY_KOTLIN_VERSION))
        settings.putString(KEY_GENERATION_SOURCE, "${info.installLocation}/templates")
        settings.putString(KEY_GENERATION_OUTPUT, "")
        settings.putBool(KEY_GENERATION_CUSTOM, false)
        settings.save(desc = "default settings")
    }


    /**
     * Updates the settings to path to templates of slate kit LATEST version
     * settings: ~/.kiit/tools/cli/conf/settings.conf
     */
    private fun update(conf: Conf, info: SetupInfo){
        val file = File(info.confDir, info.settingsName)
        val raw = Props.fromFile(file.absolutePath)
        val settings = PropSettings(raw, dir = info.confDir.absolutePath, name = info.settingsName)

        // Only update if different version
        val current = settings.getString(KEY_KIIT_VERSION)
        val custom = settings.getBool(KEY_GENERATION_CUSTOM)
        if(!custom && current != info.slatekitVersion) {
            writer.highlight("Upgrading settings at ~/.kiit/tools/cli/conf/settings.conf to ${info.slatekitVersion}")
            settings.putString(KEY_KIIT_VERSION, conf.getString(KEY_KIIT_VERSION))
            settings.putString(KEY_KIIT_VERSION_BETA, conf.getString(KEY_KIIT_VERSION_BETA))
            settings.putString(KEY_KOTLIN_VERSION, conf.getString(KEY_KOTLIN_VERSION))
            settings.putString(KEY_GENERATION_SOURCE, "${info.installLocation}/templates")
            settings.save(desc = "upgrade to ${info.slatekitVersion}")
        }
    }


    data class SetupInfo(val slatekitVersion:String, val about: About) {

        // For MAC
        val brewAppLocation = "/usr/local/Cellar/kiit/${slatekitVersion}"

        // Build folder structure from {company}/{area}/{name}
        val folders = Folders.userDir(about)

        // ~/.slatekit/tools/cli
        val pathToHOME = folders.pathToApp

        // Allow override for HOME DIR
        val homeLocation = System.getenv("KIIT_TOOLS_CLI_HOME") ?:pathToHOME

        // Allow override for INSTALL DIR ( for non *nix )
        val installLocation = System.getenv("KIIT_TOOLS_CLI_HOME") ?: brewAppLocation

        // Settings file ~/.slatekit/tools/cli/conf/settings.conf
        val settingsName = "settings.conf"

        // ~/.slatekit/tools/cli/conf
        val confDir = File(homeLocation, folders.conf)
    }


    companion object {
        const val KEY_KOTLIN_VERSION = "kotlin.version"
        const val KEY_KIIT_VERSION = "kiit.version"
        const val KEY_KIIT_VERSION_BETA = "kiit.version.beta"
        const val KEY_KIIT_VERSION_CLI = "kiit.version.cli"
        const val KEY_GENERATION_CUSTOM = "generation.custom"
        const val KEY_GENERATION_SOURCE = "generation.source"
        const val KEY_GENERATION_OUTPUT = "generation.output"
    }
}