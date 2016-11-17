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

import slate.common.results.{ResultSupportIn, ResultCode}
import slate.common.{Result, Strings, Files}

class ShellBatch(val cmd:ShellCommand, val svc:ShellService) extends ResultSupportIn {


  def run(): Unit = {

    val path = svc.folders.inputs + "/" + cmd.args.getString("file")
    val lines = Files.readAllLines(path)
    if(lines == null || lines.size == 0){
      return
    }
    val results = svc.onCommandExecuteBatch(lines, ShellConstants.BatchModeContinueOnError)
    var buffer = ""
    val newLine = Strings.newline()

    for(result <- results){

      if ( result.success && result.get != null && result.get.isInstanceOf[ShellCommand] ){
        val cmd = result.get.asInstanceOf[ShellCommand]

        if(result.success){
          buffer = buffer + "success: " + cmd.fullName + " = " + cmd.result.get.toString() + newLine
        }
        else {
          buffer = buffer + "failed: " + cmd.fullName + " = " +  cmd.result.get.toString() + newLine
        }
      }
    }
    if(svc.settings.enableOutput){
      ShellHelper.log(svc.folders, buffer)
    }
    cmd.result = ok(Some("batch output written to output directory"))
  }
}
