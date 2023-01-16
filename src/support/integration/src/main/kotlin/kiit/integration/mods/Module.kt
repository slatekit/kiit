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

package kiit.integration.mods

import kiit.common.newline
import kiit.context.Context
import kiit.results.Failure
import kiit.results.Success
import kiit.results.Try

abstract class Module(
        val appCtx: Context,
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
    open fun install(): Try<String> {
        return if (info.isDbDependent) {
            info.models?.let { models ->
                val results = models.map { modCtx.setup.install(it, info.version, "", "") }
                val success = results.all { it.success }
                val messages = results.map { it.desc }
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
    open fun uninstall(): Try<String> {
        return if (info.isDbDependent) {
            info.models?.let { models ->
                val results = models.map { modCtx.setup.uinstall(it) }
                val success = results.all { it.success }
                val messages = results.map { it.desc }
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
                    result.desc ?: modelName
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
