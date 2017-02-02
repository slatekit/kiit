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
package slate.integration

import slate.common.Funcs._
import slate.common._
import slate.common.query.Query
import slate.core.apis.{Api, ApiAction}
import slate.core.mods.{Mod, Module, ModuleContext}
import slate.core.apis.svcs.ApiWithSupport

import scala.collection.mutable.ListBuffer

@Api(area = "sys", name = "mods", desc = "management of system modules", roles= "admin", auth="key-roles", verb = "post", protocol = "*")
class ModuleApi(ctx:ModuleContext) extends ApiWithSupport {

  private val _items = new ListMap[String,Module]()


  @ApiAction(name = "", desc = "gets all installed modules", roles = "@parent" )
  def installed():List[Mod] = {
    ctx.modService.getAll()
  }


  @ApiAction(name = "", desc = "creates a new invitee", roles = "@parent" )
  def enabled():List[Mod] = {
    ctx.modService.find(new Query().where("isEnabled", "=", true))
  }


  @ApiAction(name = "", desc = "installs all modules from initial setup", roles = "@parent" )
  def install():Result[Any] = {
    val results = ListBuffer[Result[Any]]()

    // for each module, register
    for(item <- _items.all() )
    {
      val mod = item
      results.append( installMod(mod) )
    }
    val finalResult = results.reduce[Result[Any]]( (r1, r2) => r1.and(r2) )
    finalResult
  }


  @ApiAction(name = "", desc = "generates sql scripts for all models", roles = "@parent" )
  def scripts():List[String] = {
    val scripts = ListBuffer[String]()

    // for each module, register
    for(item <- _items.all() )
    {
      val mod = item
      for(script <- mod.script()){
        if(!Strings.isNullOrEmpty(script)){
          scripts.append(script)
        }
      }
    }
    scripts.toList
  }


  @ApiAction(name = "", desc = "installs a specific module", roles = "@parent" )
  def installByName(name:String): Result[Any] = {

    defaultOrExecute( _items.contains(name), failure(Some("Unknown module : " + name)), {
      val mod = _items(name)
      installMod(mod)
    })
  }


  /**
    * register a model for internal ( in-memory storage ). this should be done at startup.
    *
    * @param mod
    * @return
    */
  def register(mod:Module): Unit = {
    mod.init()
    _items.add(mod.info.name, mod)
    mod.register()
  }


  def installMod(mod:Module): Result[Any] = {

    val checkResult = ctx.modService.findFirst(new Query().where("name", "=", mod.info.name))
    if(!checkResult.isDefined){

      if(mod.info.isEnabled) {
        // Create the module entry
        val modEntry = mod.toItem()
        ctx.modService.create(modEntry)
        val updated = modEntry.copy(isInstalled = true)

        // Now let the module install itself
        mod.install()

        //if (id > 0)
        //  return success(data = id)
        //else
        //  failure("error creating : " + mod.info.name)
      }
    }
    success("already installed")
  }
}
