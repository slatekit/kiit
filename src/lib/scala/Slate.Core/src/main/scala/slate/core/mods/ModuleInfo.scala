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

case class ModuleInfo(
                    name         : String,
                    desc         : String,
                    version      : String,
                    isInstalled  : Boolean,
                    isEnabled    : Boolean,
                    isDbDependent: Boolean,
                    totalModels  : Int,
                    source       : String,
                    dependencies : String,
                    models       : Option[List[String]] = None
                 )
{

}
