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


  def run(): ShellCommand = {

    val path = svc.folders.inputs + "/" + cmd.args.getString("file")
    val lines = Files.readAllLines(path)
    if(lines == null || lines.size == 0){
      cmd
    }
    else {
      val results = svc.onCommandExecuteBatch(lines, ShellConstants.BatchModeContinueOnError)
      val newLine = Strings.newline()
      val messages = results.foldLeft("")((s, res) => {
        if (res.success) {
          val cmd = res.get
          s + "success: " + cmd.fullName + " = " + cmd.result.get.toString() + newLine
        }
        else {
          val cmd = res.get
          s + "failed: " + cmd.fullName + " = " + cmd.result.msg + newLine
        }
      })
      if (svc.settings.enableOutput) {
        ShellHelper.log(svc.folders, messages)
      }
      val batchResult = cmd.copy(result = ok(Some("batch output written to output directory")))
      batchResult
    }
  }
}
