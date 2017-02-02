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
package slate.core.mods


abstract class Module( val ctx:ModuleContext)
{
  val info:ModuleInfo

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
    if(info.isDbDependent ){
      info.models.fold(Unit)( models => {
        models.foreach( modelName => {
          ctx.modelService.install(modelName, info.version, "", "")
        })
        Unit
      })
    }
  }


  def script():List[String] = {
    if(info.isDbDependent ){
      info.models.fold(List[String]())( models => {
        models.map(modelName => {
          ctx.modelService.generateSql(modelName, info.version)
          modelName
        })
      })
    }
    else
      List[String]()
  }


  def toItem(): Mod = {
    val item = new Mod(
      name          = info.name,
      desc          = info.desc,
      version       = info.version,
      isInstalled   = info.isInstalled,
      isEnabled     = info.isEnabled,
      isDbDependent = info.isDbDependent,
      totalModels   = info.totalModels,
      source        = info.source,
      dependencies  = info.dependencies
    )
    item
  }
}
