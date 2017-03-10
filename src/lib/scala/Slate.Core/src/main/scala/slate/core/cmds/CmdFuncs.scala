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

import slate.common.{Funcs, Result, DateTime}
import slate.common.DateTime._

object CmdFuncs {


  /**
   * builds a default Command State
   * @param name
   * @return
   */
  def defaultState(name:String): CmdState = {

    CmdState (
      name       = name,
      msg        = "Not yet run",
      lastRuntime= DateTime.min(),
      hasRun     = false,
      runCount   = 0,
      errorCount = 0,
      lastResult = None
    )
  }


  /**
   * Builds a default Command Result
   * @param name   : The name of the command
   * @return
   */
  def defaultResult(name:String):CmdResult = {

    // The result
    val cmdResult = CmdResult (
      name    = name,
      success = false,
      message = None ,
      error   = None ,
      result  = None ,
      started = DateTime.min(),
      ended   = DateTime.min(),
      totalMs = 0
    )
    cmdResult
  }


  /**
    * builds an error CommandState
    * @param name
    * @param message
    * @return
    */
  def errorState(name:String, message:String): CmdState = {
    CmdState(name, message, DateTime.min(), false, 0, 0, None)
  }


  /**
    * builds an error Command Result
    * @param name
    * @param message
    * @return
    */
  def errorResult(name:String, message:String): CmdResult = {
    CmdResult(name, false, Option(message), None, None, now(), now(), 0)
  }


  /**
   * Converts an Tuple to the CmdResult
   * @param name   : The name of the command
   * @param start  : The start time of the command execution
   * @param end    : The end time of the command execution
   * @param result : The result of the command in tuple form
   * @return
   */
  def fromResult(name:String, start:DateTime, end:DateTime,
                 result:Result[Any]):CmdResult = {

    // The result
    val cmdResult = CmdResult (
      name    = name,
      success = result.success,
      message = result.msg,
      error   = result.err,
      result  = result.fold[Option[Any]](None)( r => Funcs.flatten(r)),
      started = start,
      ended   = end,
      totalMs = end.durationFrom(start).toMillis
    )
    cmdResult
  }
}
