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
package slate.core.cmds

import slate.common.DateTime


/**
  *
  * @param name        : Name of the command
  * @param lastRuntime : Last time the command was run
  * @param hasRun      : Whether command has run at least once
  * @param runCount    : The total times the command was run
  * @param errorCount  : The total errors
  * @param lastResult  : The last result
  */
case class CmdState(
                     name       : String,
                     msg        : String,
                     lastRuntime: DateTime,
                     hasRun     : Boolean,
                     runCount   : Int,
                     errorCount : Int,
                     lastResult : Option[CmdResult]
              )
{
  /**
   * Builds a copy of the this state with bumped up numbers ( run count, error count, etc )
   * based on the last execution result
   * @param result
   * @return
   */
  def update(result:CmdResult): CmdState = {

    val updated = this.copy(
      msg = result.message.getOrElse(""),
      lastRuntime = result.started,
      hasRun = true,
      runCount = runCount + 1,
      errorCount = errorCount + result.error.fold(0)(_ => 1),
      lastResult = Option(result)
    )
    updated
  }
}
