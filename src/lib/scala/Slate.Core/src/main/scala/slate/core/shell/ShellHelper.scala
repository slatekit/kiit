/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
</slate_header>
  */

package slate.core.shell


import slate.common.args.ArgsHelper
import slate.common.info.Folders
import slate.common.{Strings, BoolMessage, Files}


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
