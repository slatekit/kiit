
/**
  * <slate_header>
  * url: www.slatekit.com
  * git: www.github.com/code-helix/slatekit
  * org: www.codehelix.co
  * author: Kishore Reddy
  * copyright: 2016 CodeHelix Solutions Inc.
  * license: refer to website and/or github
  * about: A Scala utility library, tool-kit and server backend.
  * mantra: Simplicity above all else
  * </slate_header>
  */

package slate.core.cmds

import java.util.concurrent.atomic.AtomicReference

import slate.common.DateTime._
import slate.common.Result
import slate.common.results.ResultFuncs._


/**
 * Light-weight implementation of a command-like pattern that can execute some code
 * and track the last result of that code.
 *
 * NOTES:
 * 1. The code to execute can be a function that you pass in
 * 2. You can also derive this class and override the executeInternal method
 * 3. The result (CmdResult) of an execution is stored in the lastResult
 * 4. The result has the following info
 *
 *    - name of the command
 *    - success/failure of the command
 *    - message of success/failure
 *    - result of the last command
 *    - time started
 *    - time ended
 *    - duration of the execution
 *
 * The commands can be registered with the Cmds component and you track the last time
 * a command was run.
 * @param name
  */
class Cmd(val name: String,
          val desc: Option[String] = None ,
          call: Option[(Option[Array[String]]) => Option[Any]] = None) {


  private val _lastResult = new AtomicReference[CmdResult](CmdFuncs.defaultResult(name))
  private val _lastStatus = new AtomicReference[CmdState](CmdFuncs.defaultState(name))


  /**
   * Expose the immutable last execution result of this command
   * @return
   */
  def lastResult():CmdResult = _lastResult.get


  /**
   * Expose the last known status of this command
   * @return
   */
  def lastStatus():CmdState  = _lastStatus.get


  /**
   * execute this command with optional arguments
    *
    * @param args
   * @return
   */
  final def execute(args:Option[Array[String]] = None): CmdResult =
  {
    // Track time
    val start = now()

    // Result
    val result:Result[Any] =
      try {

        // Either call the function supplied or assume
        // the derived class has implemented executeInternal
        call.fold[Result[Any]](executeInternal(args))( c => {
          success[Any](c(args))
        })
      }
      catch{
        case ex:Exception => {
          failure[Any](Some("Error while executing : " + name + ". " + ex.getMessage), Some(ex))
        }
      }

    // Stop tracking time (inclusive of possible error )
    val end = now()

    // The result
    val cmdResult = CmdFuncs.fromResult(name, start, end, result)

    // Track the last result and build updated status
    _lastResult.set(cmdResult)
    _lastStatus.set(_lastStatus.get().update(cmdResult))

    cmdResult
  }


  /**
   * executes the command, this should be overridden in sub-classes
    *
    * @param args
   * @return
   */
  protected def executeInternal(args: Option[Array[String]]) : Result[Any] = notImplemented()
}
