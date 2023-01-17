/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * 
 * 
 *  </kiit_header>
 */
package kiit.integration.apis

import kiit.integration.mods.Module
import kiit.apis.Api
import kiit.apis.Action
import kiit.apis.AuthModes
import kiit.apis.Verbs
import kiit.common.*
import kiit.common.crypto.Encryptor
import kiit.common.log.Logger
import kiit.common.values.ListMap
import kiit.context.Context
import kiit.integration.mods.Mod
import kiit.query.Op
import kiit.results.Failure
import kiit.results.Notice
import kiit.results.Success
import kiit.results.Try

/**
 * The modules service supports the install of a "feature" module.
 * A feature module is a logical feature/product area of an application. It can:
 * 1. Optionally contain 1 or more @see[kiit.entities.Entity] that is mapped to a database table
 * 2. Can be installed as a table to the database
 * 3. Can be uninstalled as a table in the database
 * 4. Registered so that we know what modules are set up
 *
 * This service provides the functionality for installing, uninstalling, feature modules.
 */
@Api(area = "setup", name = "modules", desc = "management of system modules",
        auth = AuthModes.KEYED, roles = ["admin"], verb = Verbs.AUTO, sources = [Sources.ALL])
class ModuleApi(val ctx: kiit.integration.mods.ModuleContext, override val context: Context) : kiit.apis.support.FileSupport {

    override val encryptor: Encryptor? = context.enc
    override val logger: Logger? = context.logs.getLogger()

    private var _items = ListMap<String, Module>()

    /**
     * Creates the `mod` table in the database for storing all installed modules
     */
    @Action(desc = "sets up the db to support modules")
    suspend fun setupDb(): Try<Any> {
        return ctx.setup.install(kiit.integration.mods.Mod::class.qualifiedName!!, "1", "", "")
    }

    /**
     * Gets the names of all modules in the database
     */
    @Action(desc = "gets the names of the modules")
    fun names(): List<String> {
        return _items.all().map { "${it.info.name} ver: ${it.info.version}" }
    }

    /**
     * Gets a list of all installed modules ( from the `module` table in the database )
     */
    @Action(desc = "gets all installed modules")
    suspend fun installed(): List<kiit.integration.mods.Mod> {
        return ctx.service.getAll()
    }

    /**
     * Get a list of all installed modules in the database
     */
    @Action(desc = "gets all enabled modules")
    suspend fun enabled(): List<kiit.integration.mods.Mod> {
        return ctx.service.find { where(Mod::isEnabled.name, Op.Eq, true) }
    }

    /**
     * Installs all modules.
     * Notes:
     * 1. This installs tables associated with any entities linked to the module
     * 2. Calls the install method on the module
     */
    @Action(desc = "installs all modules from initial setup")
    suspend fun installAll(): Notice<Any> {
        val res = _items.all().map { module -> installUpdate(module, false) }
        val finalResult = res.reduce({ acc, item -> if (!acc.success) acc else item })
        return finalResult
    }

    /**
     * Installs only a single module based on its name supplied
     * @param name: The fully qualified name of the module ( @see[kiit.integration.mods.ModuleInfo.name]
     */
    @Action(desc = "installs a specific module")
    suspend fun installByName(name: String): Notice<Any> {
        return _items[name]?.let { installUpdate(it, false) } ?: Failure("Unknown module : $name")
    }

    /**
     * Generates a sql script containing the DDL sql statements for all entities
     */
    @Action(desc = "generates sql scripts for all models")
    fun script(): List<String> {
        val scripts = _items.all().map { module ->
            val sqlScript = module.script()
            sqlScript
        }
        return scripts.flatten()
    }

    /**
     * Force installs a specific module
     */
    @Action(desc = "forces the install of a specific module or updates it. updates the mod entry and creates table")
    suspend fun forceInstallByName(name: String): Notice<Any> {
        return _items[name]?.let { installUpdate(it, true) } ?: Failure("Unknown module : $name")
    }

    /**
     * Uninstalls all modules ( this is destructive ) and will delete all data/tables.
     * Should only be used for administrative purposes and initial setup
     */
    @Action(desc = "gets the names of the modules")
    suspend fun uninstallAll(): Try<String> {
        val all = _items.all().map { it.info.name }
        val results = all.map { name -> uninstallByName(name) }
        val success = results.foldRight(true, { res, acc -> if (!res.success) false else acc })
        val message = results.map({ result -> if (result.success) "" else result.desc }).joinToString { newline }
        val result = if (success) Success("Uninstalled all") else Failure(Exception(message))
        return result
    }

    @Action(desc = "gets the names of the modules")
    suspend fun uninstallByName(name: String): Try<String> {
        val tablesResult = _items[name]?.let { it.uninstall() } ?: Failure("Unknown module : $name").toTry()
        val moduleResult = tablesResult.map { ctx.service.deleteByField(Mod::name, name) }
        return when (moduleResult) {
            is Success -> Success("Removed module and tables for : $name")
            is Failure -> Failure(moduleResult.error)
        }
    }

    /**
     * register a model for internal ( in-memory storage ). this should be done at startup.
     *
     * @param mod
     * @return
     */
    fun register(mod: Module) {
        mod.init()
        _items = _items.add(mod.info.name, mod)
        mod.register()
    }

    /**
     * Seeds all modules by calling its seed method.
     */
    @Action(desc = "seeds all the modules")
    fun seed(): Notice<Any> {
        val res = _items.all().map { module -> seed(module) }
        val finalResult = res.reduce { acc, item -> if (!acc.success) acc else item }
        return finalResult
    }

    @Action(desc = "seeds all the modules")
    fun seedModule(name: String): Notice<Any> {
        return _items[name]?.let { seed(it) } ?: Failure("Unknown module : $name")
    }

    /**
     * register a model for internal ( in-memory storage ). this should be done at startup.
     *
     * @param mod
     * @return
     */
    private fun seed(mod: Module): Notice<Any> {
        val result = try {
            mod.seed()
            Success("Seeded module: " + mod.info.name)
        } catch (ex: Exception) {
            this.logger?.error("Error seeding: ${mod.info.name}", ex)
            Failure("Error seeding module: " + mod.info.name)
        }
        return result
    }

    suspend fun installUpdate(mod: kiit.integration.mods.Module, updateIfPresent: Boolean = false): Notice<Any> {

        val checkResult = ctx.service.find { where("name", Op.Eq, mod.info.name) }.firstOrNull()
        if (checkResult == null) {

            if (mod.info.isEnabled) {
                // Create the module entry
                val modEntry = mod.toItem()
                val updated = modEntry.copy(isInstalled = true)
                ctx.service.create(updated)

                // Now let the module install itself
                mod.install()

                // if (id > 0)
                //  return success(data = id)
                // else
                //  failure("error creating : " + mod.info.name)
            }
        } else if (updateIfPresent) {
            checkResult.let { modentry ->
                ctx.service.update(modentry)
                mod.install()
            }
        }
        return Success("installed")
    }
}
