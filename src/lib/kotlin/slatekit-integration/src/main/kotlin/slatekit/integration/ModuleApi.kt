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
package slatekit.integration

import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.apis.svcs.ApiWithSupport
import slatekit.common.ListMap
import slatekit.common.Result
import slatekit.common.query.Query
import slatekit.common.results.ResultFuncs.failure
import slatekit.common.results.ResultFuncs.success
import slatekit.core.common.AppContext
import slatekit.core.mods.Mod
import slatekit.core.mods.Module
import slatekit.core.mods.ModuleContext


@Api(area = "sys", name = "mods", desc = "management of system modules", roles = "admin", auth = "key-roles", verb = "post", protocol = "*")
class ModuleApi(val ctx: ModuleContext,
                context: AppContext) : ApiWithSupport(context) {

    private var _items = ListMap<String, Module>()


    @ApiAction(name = "", desc = "sets up the db to support modules", roles = "@parent")
    fun setupDb(): Result<Any> {
        return ctx.setup.install(Mod::class.qualifiedName!!, "1", "", "")
    }


    @ApiAction(name = "", desc = "gets all installed modules", roles = "@parent")
    fun installed(): List<Mod> {
        return ctx.service.getAll()
    }


    @ApiAction(name = "", desc = "creates a new invitee", roles = "@parent")
    fun enabled(): List<Mod> {
        return ctx.service.find(Query().where("isEnabled", "=", true))
    }


    @ApiAction(name = "", desc = "installs all modules from initial setup", roles = "@parent")
    fun install(): Result<Any> {
        val res = _items.all().map { module -> installMod(module) }
        val finalResult = res.reduce({ acc, item -> if (!acc.success) acc else item })
        return finalResult
    }


    @ApiAction(name = "", desc = "generates sql scripts for all models", roles = "@parent")
    fun script(): List<String> {
        val scripts = _items.all().map { module ->
            val sqlScript = module.script()
            sqlScript
        }
        return scripts.flatten()
    }


    @ApiAction(name = "", desc = "installs a specific module", roles = "@parent")
    fun installByName(name: String): Result<Any> {
        return _items[name]?.let { installMod(it) } ?: failure("Unknown module : " + name)
    }


    @ApiAction(name = "", desc = "gets the names of the modules", roles = "@parent")
    fun names(name: String): List<String> {
        return _items.all().map { "${it.info.name} ver: ${it.info.version}" }
    }


    /**
     * register a model for internal ( in-memory storage ). this should be done at startup.
     *
     * @param mod
     * @return
     */
    fun register(mod: Module): Unit {
        mod.init()
        _items = _items.add(mod.info.name, mod)
        mod.register()
    }


    fun installMod(mod: Module): Result<Any> {

        val checkResult = ctx.service.findFirst(Query().where("name", "=", mod.info.name))
        if (checkResult == null) {

            if (mod.info.isEnabled) {
                // Create the module entry
                val modEntry = mod.toItem()
                val updated = modEntry.copy(isInstalled = true)
                ctx.service.create(updated)

                // Now let the module install itself
                mod.install()

                //if (id > 0)
                //  return success(data = id)
                //else
                //  failure("error creating : " + mod.info.name)
            }
        }
        return success("installed")
    }
}
