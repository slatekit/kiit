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

package slatekit.integration.mods

import slatekit.common.Failure
import slatekit.common.ResultEx
import slatekit.common.Success
import slatekit.common.newline
import slatekit.core.common.AppContext

abstract class Module(
    val appCtx: AppContext,
    val modCtx: ModuleContext
) {
    abstract val info: ModuleInfo

    /**
     * hook to initialize
     */
    open fun init() {}

    open fun register() {}

    /**
     * install this module
     */
    open fun install(): ResultEx<String> {
        return if (info.isDbDependent) {
            info.models?.let { models ->
                val results = models.map { modCtx.setup.install(it, info.version, "", "") }
                val success = results.all { it.success }
                val messages = results.map { it.msg }
                val message = if (success) "" else messages.joinToString(newline)
                if (success) Success(message, msg = "") else Failure(Exception(message), msg = message)
            } ?: Failure(Exception(this.info.name + " has no models"))
        } else {
            Failure(Exception(this.info.name + " is not database dependent"))
        }
    }

    /**
     * install this module
     */
    open fun uninstall(): ResultEx<String> {
        return if (info.isDbDependent) {
            info.models?.let { models ->
                val results = models.map { modCtx.setup.uinstall(it) }
                val success = results.all { it.success }
                val messages = results.map { it.msg }
                val message = if (success) "" else messages.joinToString(newline)
                if (success) Success(message, msg = "") else Failure(Exception(message), msg = message)
            } ?: Failure(Exception(this.info.name + " has no models"))
        } else {
            Failure(Exception(this.info.name + " is not database dependent"))
        }
    }

    open fun seed() {
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
        } else
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
