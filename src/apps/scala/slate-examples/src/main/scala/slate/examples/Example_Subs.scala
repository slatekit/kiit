/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2016 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.examples

import slate.common.subs.Subs
import slate.core.cmds.Cmd

/**
  * Created by kreddy on 3/21/2016.
  */
class Example_Subs  extends Cmd("sms") {

  override protected def executeInternal(args: Any) : AnyRef =
  {
    //<doc:setup>
    val subs = new Subs()
    subs("user.home"    ) = (s) => "c:/users/johndoe"
    subs("company.id"   ) = (s) => "slatekit"
    subs("company.dir"  ) = (s) => "@{user.home}/@{company.id}"
    subs("company.confs") = (s) => "@{user.home}/@{company.id}/confs"
    subs("app.id"       ) = (s) => "slatekit.tests"
    subs("app.dir"      ) = (s) => "@{company.dir}/@{app.id}"
    subs("app.confs"    ) = (s) => "@{app.dir}/confs"
    subs("user.name"    ) = (s) => "john.doe"
    //</doc:setup>

    //<doc:examples>
    val home = subs("@{user.home}")
    val path = subs("@{company.dir}")
    val log  = subs("@{app.id}/logs/log.txt")
    //</doc:examples>
    result
  }
}
