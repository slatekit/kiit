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

package slatekit.core.mods

import slatekit.core.common.AppContext

abstract class Module(val appCtx: AppContext,
                      val modCtx: ModuleContext) {
    abstract val info: ModuleInfo

    /**
     * hook to initialize
     */
    open fun init(): Unit {}


    open fun register(): Unit {}


    /**
     * install this module
     */
    open fun install(): Unit {
        if (info.isDbDependent) {
            info.models?.let { models ->
                models.forEach { modelName ->
                    modCtx.setup.install(modelName, info.version, "", "")
                }
            }
        }
    }


    /**
     * script out all the sql for all the modules.
     */
    open fun script(): List<String> {
        val result = if (info.isDbDependent) {
            info.models?.let { models ->
                models.map { modelName ->
                    val result = modCtx.setup.generateSql(modelName, info.version)
                    result.msg ?: modelName
                }
            } ?: listOf<String>()
        }
        else
            listOf<String>()

        return result
    }


    fun toItem(): Mod {
        val item = Mod(
                name = info.name,
                desc = info.desc,
                version = info.version,
                isInstalled = info.isInstalled,
                isEnabled = info.isEnabled,
                isDbDependent = info.isDbDependent,
                totalModels = info.totalModels,
                source = info.source,
                dependencies = info.dependencies
        )
        return item
    }
}