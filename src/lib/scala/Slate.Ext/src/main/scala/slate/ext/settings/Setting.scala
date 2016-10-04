/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.ext.settings

import slate.common.{Field, DateTime}
import slate.entities.core.{IEntityUnique, IEntity}
import slate.core.common.tenants.ITenant

class Setting extends IEntity with IEntityUnique with ITenant
{
  var id = 0L

  @Field("",true, 50)
  var uniqueId: String = ""


  @Field("", true, -1)
  var tenantId = 0


  /** uniquely identifies a group
    *
    * @example : "web" | "task" | "job"
    */
  @Field("", true, 30)
  var group = ""


  /** name of the application or service
    *
    * @example : users | sharing | reg |
    */
  @Field("", true, 30)
  var name = ""


  @Field("", true, 30)
  var valueType = ""


  @Field("", true, 30)
  var valueDefault = ""


  @Field("", true, 150)
  var value = ""


  @Field("", true, -1)
  var createdAt  = DateTime.now()


  @Field("", true, -1)
  var createdBy  = 0


  @Field("", true, -1)
  var updatedAt  =  DateTime.now()


  @Field("", true, -1)
  var updatedBy  = 0
}
