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

package slate.common.app

import slate.common.DateTime

case class AppRunState(
                        name          : String   = ""             ,
                        lastRunTime   : DateTime = DateTime.now() ,
                        status        : String   = ""             ,
                        runCount      : Int      = 0              ,
                        errorCount    : Int      = 0              ,
                        lastResult    : String   = ""
                     )
{
}
