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
                     lastRuntime: DateTime,
                     hasRun     : Boolean,
                     runCount   : Int,
                     errorCount : Int,
                     lastResult : CmdResult
              )
{
}
