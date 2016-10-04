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

package slate.common.info

import slate.common.{TimeSpan, DateTime}

class Status (
                var started    : DateTime = DateTime.now()    ,
                var ended      : DateTime = DateTime.now()    ,
                var duration   : TimeSpan = TimeSpan(0,0,0)   ,
                var status     : String   = "not-started"     ,
                var errors     : Int      = 0                 ,
                var error      : String   = "n/a"
             )
{

}


object Status
{
  val none = new Status()
}