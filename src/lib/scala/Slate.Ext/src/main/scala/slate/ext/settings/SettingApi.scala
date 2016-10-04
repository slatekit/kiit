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

import slate.core.apis.{ApiAction, Api}
import slate.core.common.svcs.ApiEntityWithSupport
import scala.reflect.runtime.universe.typeOf

@Api(area = "app", name = "settings", desc = "settings for application",
  roles= "@admin", auth = "app", verb = "*", protocol = "*")
class SettingApi extends ApiEntityWithSupport[Setting, SettingService] {

  @ApiAction(name = "", desc= "creates a new setting", roles= "@parent")
  def create(group:String, name:String, valueDefault:String, valueType:String, value:String):Unit = {
    service.create(group, name, valueDefault, valueType, value)
  }


  override def init():Unit =
  {
    val svc = context.ent.getService(typeOf[Setting]).asInstanceOf[SettingService]
    initContext(svc)
  }
}
