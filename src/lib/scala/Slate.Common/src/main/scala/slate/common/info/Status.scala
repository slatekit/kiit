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

case class Status (
                    started    : DateTime = DateTime.now()    ,
                    ended      : DateTime = DateTime.now()    ,
                    duration   : TimeSpan = TimeSpan(0,0,0)   ,
                    status     : String   = "not-started"     ,
                    errors     : Int      = 0                 ,
                    error      : String   = "n/a"
                  )
{
  def start(statusName:Option[String] = None): Status = {
    copy(started = DateTime.now(), status = statusName.getOrElse("started"))
  }


  def error(msg:String):Status = {
    copy(error = msg, errors = errors + 1)
  }


  def end(statusName:Option[String] = None): Status = {
    copy(started = DateTime.now(), status = statusName.getOrElse("ended"))
  }
}


object Status
{
  val none = new Status()
}