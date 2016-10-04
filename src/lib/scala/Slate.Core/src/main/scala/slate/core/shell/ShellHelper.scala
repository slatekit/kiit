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

package slate.core.shell


import slate.common.info.Folders
import slate.common.{Files}


object ShellHelper {

  def log(folders:Folders, cmd:ShellCommand, content:String):Unit=
  {
    Files.writeFileForDateAsTimeStamp(folders.pathToLogs, content)
  }


  def log(folders:Folders, content:String):Unit=
  {
    Files.writeFileForDateAsTimeStamp(folders.pathToLogs, content)
  }
}
