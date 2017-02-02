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

import slate.common.{Field, DateTime}
import slate.entities.core._

import scala.annotation.meta.field

case class Mod(
                @(Field@field)("",true, 50)
                id: Long = 0L,


                @(Field@field)("", true, 50)
                name: String  = "",


                @(Field@field)("", true, 200)
                desc: String = "",


                @(Field@field)("", true, 30)
                version: String = "",


                @(Field@field)("", true, -1)
                isInstalled:Boolean   = false,


                @(Field@field)("", true, -1)
                isEnabled:Boolean     = false,


                @(Field@field)("", true, -1)
                isDbDependent:Boolean = false,


                @(Field@field)("", true, -1)
                totalModels:Int  = 0,


                @(Field@field)("", true, 50)
                source:String  = "",


                @(Field@field)("", true, 100)
                dependencies:String  = "",


                @(Field@field)("", true, -1)
                createdAt:DateTime  = DateTime.now(),


                @(Field@field)("", true, -1)
                createdBy:Int  = 0,


                @(Field@field)("", true, -1)
                updatedAt:DateTime  =  DateTime.now(),


                @(Field@field)("", true, -1)
                updatedBy:Int  = 0,


                @(Field@field)("",true, 50)
                uniqueId: String = ""
              )
  extends IEntity with IEntityUnique
{
}
