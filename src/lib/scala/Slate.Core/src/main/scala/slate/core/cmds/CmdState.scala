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
class CmdState(
                 var name       : String,
                 var lastRuntime: DateTime,
                 var hasRun     : Boolean,
                 var runCount   : Int,
                 var errorCount : Int,
                 var lastResult : CmdResult
              )
{

  /**
   * creates a copy of the current state.
   * NOTE: This is a not a case class because the variables representing the state are mutable
   * by the owner of the state ( The command )
   *
   * The state is never available to a caller ( it is copied and sent to the caller )
   * @return
   */
  def copy(): CmdState =
  {
    val state = new CmdState(name, lastRuntime, hasRun, runCount, errorCount, lastResult)
    state
  }
}
