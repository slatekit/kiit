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
package slate.core.common.tenants

import slate.common.Field
import slate.entities.core.EntityUnique

class Tenant extends EntityUnique {

  def this(name:String, appName:String) = {
    this()
    this.name = name
    this.folder = appName
  }


  @Field("", true, 30)
  var name          = ""


  @Field("", true, 30)
  var desc          = ""


  @Field("", true, 30)
  var key       = ""


  @Field("", true, 30)
  var folder     = ""


  @Field("", true, -1)
  var isEnabled     = false


  @Field("", true, 50)
  var account        = ""


  @Field("", true, 50)
  var contact        = ""


  @Field("", true, 50)
  var url        = ""
}
