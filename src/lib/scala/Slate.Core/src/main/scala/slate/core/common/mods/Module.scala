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
package slate.core.common.mods

import slate.common.Result

import scala.collection.mutable.ListBuffer


class Module
{
  var ctx:ModuleContext = null

  /**
    * module info setup on init of derived modules
    */
  var info:ModuleInfo = null


  /**
    * hook to initialize
    */
  def init():Unit = {
  }


  def register():Unit = {

  }


  /**
    * install this module
    */
  def install():Unit = {
    if(info.isDbDependent && info.models.isDefined ){
      for(modelName <- info.models.get ) {
        ctx.modelService.install(modelName, info.version, "", "")
      }
    }
  }


  def script():List[String] = {
    val scripts = new ListBuffer[String]()
    if(info.isDbDependent && info.models.isDefined ){
      for(modelName <- info.models.get ) {
        ctx.modelService.generateSql(modelName, info.version)
        scripts.append(modelName)
      }
    }
    scripts.toList
  }


  def toItem(): Mod = {
    val item = new Mod()
    item.name          = info.name
    item.desc          = info.desc
    item.version       = info.version
    item.isInstalled   = info.isInstalled
    item.isEnabled     = info.isEnabled
    item.isDbDependent = info.isDbDependent
    item.totalModels   = info.totalModels
    item.source        = info.source
    item.dependencies  = info.dependencies
    item
  }
}
