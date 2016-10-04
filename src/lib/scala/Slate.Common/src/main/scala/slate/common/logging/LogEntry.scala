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


import slate.common.logging.LogLevel.LogLevel


class LogEntry {
  var level : LogLevel = LogLevel.Info
  var msg : Option[String] = None
  var ex: Option[Exception] = None
}
