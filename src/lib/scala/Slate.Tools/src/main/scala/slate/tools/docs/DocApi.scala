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

package slate.tools.docs

import slate.common.Result
import slate.common.results.{ResultSupportIn}
import slate.core.apis.{Api, ApiAction}
import slate.core.common.svcs.ApiWithSupport

@Api(area = "sys", name = "docs", desc= "help doc generator",
  roles= "@admin", auth = "app", verb = "post", protocol = "*")
class DocApi extends ApiWithSupport
  with ResultSupportIn {


  @ApiAction(name = "", desc= "encryptes the text", roles= "", verb = "@parent", protocol = "@parent")
  def generate(root:String, output:String, template:String):Result[String] =
  {
    val doc = new DocService(root, output, template)
    val result = doc.process()
    result
  }
}
