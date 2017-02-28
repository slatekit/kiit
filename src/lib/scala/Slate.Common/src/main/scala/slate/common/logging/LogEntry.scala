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

package slate.common.logging

import java.time.ZoneOffset

import slate.common.DateTime

case class LogEntry(
                      name  : String            = "",
                      level : LogLevel          = Info,
                      msg   : String            = "",
                      time  : DateTime          = DateTime.now,
                      ex    : Option[Exception] = None,
                      tag   : Option[String]    = None
                   )
{
}
