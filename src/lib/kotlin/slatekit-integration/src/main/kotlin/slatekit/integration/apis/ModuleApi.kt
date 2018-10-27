/**
 * <slate_header>
 * author: Kishore Reddy
 * url: https://github.com/kishorereddy/scala-slate
 * copyright: 2016 Kishore Reddy
 * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
 * desc: a scala micro-framework
 * usage: Please refer to license on github for more info.
 * </slate_header>
 */
package slatekit.integration.apis

import slatekit.integration.mods.Module
import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.apis.security.AuthModes
import slatekit.apis.security.Protocols
import slatekit.apis.security.Verbs
import slatekit.common.*
import slatekit.common.utils.ListMap
import slatekit.integration.mods.Mod

@Api(area = "setup", name = "modules", desc = "management of system modules",
        auth = AuthModes.apiKey, roles = "admin", verb = Verbs.auto, protocol = Protocols.all)
class ModuleApi(val ctx: slatekit.integration.mods.ModuleContext, override val context: slatekit.core.common.AppContext) : slatekit.apis.support.ApiWithSupport {

    private var _items = ListMap<String, Module>()

    @ApiAction(desc = "sets up the db to support modules")
    fun setupDb(): slatekit.common.ResultEx<Any> {
        return ctx.setup.install(slatekit.integration.mods.Mod::class.qualifiedName!!, "1", "", "")
    }

    @ApiAction(desc = "gets all installed modules")
    fun installed(): List<slatekit.integration.mods.Mod> {
        return ctx.service.getAll()
    }

    @ApiAction(desc = "creates a new invitee")
    fun enabled(): List<slatekit.integration.mods.Mod> {
        return ctx.service.find(slatekit.common.query.Query().where("isEnabled", "=", true))
    }

    @ApiAction(desc = "installs all modules from initial setup")
    fun install(): slatekit.common.ResultMsg<Any> {
        val res = _items.all().map { module -> installUpdate(module, false) }
        val finalResult = res.reduce({ acc, item -> if (!acc.success) acc else item })
        return finalResult
    }

    @ApiAction(desc = "generates sql scripts for all models")
    fun script(): List<String> {
        val scripts = _items.all().map { module ->
            val sqlScript = module.script()
            sqlScript
        }
        return scripts.flatten()
    }

    @ApiAction(desc = "installs a specific module")
    fun installByName(name: String): slatekit.common.ResultMsg<Any> {
        return _items[name]?.let { installUpdate(it, false) } ?: slatekit.common.results.ResultFuncs.failure("Unknown module : " + name)
    }

    @ApiAction(desc = "forces the install of a specific module or updates it. updates the mod entry and creates table")
    fun forceInstallByName(name: String): slatekit.common.ResultMsg<Any> {
        return _items[name]?.let { installUpdate(it, true) } ?: slatekit.common.results.ResultFuncs.failure("Unknown module : " + name)
    }

    @ApiAction(desc = "gets the names of the modules")
    fun names(): List<String> {
        return _items.all().map { "${it.info.name} ver: ${it.info.version}" }
    }

    @ApiAction(desc = "gets the names of the modules")
    fun uninstallAll(): ResultEx<String> {
        val all = _items.all().map { it.info.name }
        val results = all.map { name -> uninstall(name) }
        val success = results.foldRight(true, { res, acc -> if (!res.success) false else acc })
        val message = results.map({ result -> if (result.success) "" else result.msg }).joinToString { newline }
        val result = if (success) Success("Uninstalled all") else Failure(Exception(message))
        return result
    }

    @ApiAction(desc = "gets the names of the modules")
    fun uninstall(name: String): ResultEx<String> {
        val tablesResult = _items[name]?.let { it.uninstall() } ?: Failure("Unknown module : $name").toResultEx()
        val moduleResult = tablesResult.map { ctx.service.deleteByField(Mod::name, name) }
        return when (moduleResult) {
            is Success -> Success("Removed module and tables for : $name")
            is Failure -> Failure(moduleResult.err)
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

    @ApiAction(desc = "seeds all the modules")
    fun seed(): slatekit.common.ResultMsg<Any> {
        val res = _items.all().map { module -> seedModule(module) }
        val finalResult = res.reduce({ acc, item -> if (!acc.success) acc else item })
        return finalResult
    }

    /**
     * register a model for internal ( in-memory storage ). this should be done at startup.
     *
     * @param mod
     * @return
     */
    fun seedModule(mod: Module): ResultMsg<Any> {
        val result = try {
            mod.seed()
            Success("Seeded module: " + mod.info.name)
        } catch (ex: Exception) {
            this.logger?.error("Error seeding: ${mod.info.name}", ex)
            Failure("Error seeding module: " + mod.info.name)
        }
        return result
    }

    fun installUpdate(mod: slatekit.integration.mods.Module, updateIfPresent: Boolean = false): slatekit.common.ResultMsg<Any> {

        val checkResult = ctx.service.findFirst(slatekit.common.query.Query().where("name", "=", mod.info.name))
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
        return slatekit.common.results.ResultFuncs.success("installed")
    }
}
