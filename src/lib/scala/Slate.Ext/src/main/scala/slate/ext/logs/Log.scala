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
package slate.ext.logs

import slate.common.{Field, DateTime}
import slate.entities.core.{IEntityUnique, IEntity}
import slate.core.common.tenants.ITenant

class Log extends IEntity with IEntityUnique with ITenant
{
  var id = 0L

  @Field("",true, 50)
  var uniqueId: String = ""


  @Field("", true, -1)
  var tenantId = 0


  @Field("", true, 10)
  var level = ""


  @Field("", true, 50)
  var logger = ""


  @Field("", true, 100)
  var message = ""


  @Field("", true, 2000)
  var exception = ""


  @Field("", true, -1)
  var createdAt  = DateTime.now()


  @Field("", true, -1)
  var createdBy  = 0


  @Field("", true, -1)
  var updatedAt  =  DateTime.now()


  @Field("", true, -1)
  var updatedBy  = 0
}
