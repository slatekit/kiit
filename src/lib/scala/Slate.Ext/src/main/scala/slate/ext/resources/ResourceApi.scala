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

package slate.ext.resources


import slate.core.apis.{Api, ApiAction}
import slate.core.common.svcs.ApiEntityWithSupport
import scala.reflect.runtime.universe.typeOf


@Api(area = "sys", name = "resources", desc = "api to access and manage system resources",
  roles= "@admin", auth = "app", verb = "*", protocol = "*")
class ResourceApi extends ApiEntityWithSupport[Resource, ResourceService] {


    @ApiAction(name = "", desc= "sets up the default resources", roles= "@parent")
    def applyDefaults():Unit =
    {
      service.applyDefaults()
    }


    @ApiAction(name = "", desc="get queues by country", roles= "@parent")
    def queues(country:String):List[Resource] =
    {
      service.queues(country)
    }


    @ApiAction(name = "", desc="get servers by country", roles= "@parent")
    def servers(country:String):List[Resource] =
    {
      service.servers(country)
    }


    override def init():Unit =
    {
      val svc = context.ent.getService(typeOf[Resource]).asInstanceOf[ResourceService]
      initContext(svc)
    }
}
