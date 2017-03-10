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

import slate.common.{DateTime, Result}
import slate.common.results.ResultFuncs._

/**
  * Command manager to run commands and get back the status of each command
  * and their last results.
  * @param cmds
  */
class Cmds(cmds:List[Cmd]) {

  /**
    * Create a lookup of command name to command
    */
  val cmdLookup = cmds.map( cmd => cmd.name -> cmd).toMap


  /**
   * names of commands
   */
  def names:List[String] = cmds.map( cmd => cmd.name )


  /**
   * number of commands
   */
  def size:Int = cmdLookup.size


  /**
    * whether or not there is a command with the supplied name.
    * @param name
    * @return
    */
  def contains(name:String): Boolean = cmdLookup.contains(name)


  /**
    * Runs the command with the supplied name
    * @param name
    * @return
    */
  def run(name:String, args:Option[Array[String]]): CmdResult = {
    val result =
      if(!contains(name)){
        CmdFuncs.errorResult(name, "Not found")
      }
      else {
        // Get result
        cmdLookup(name).execute(args)
      }
    result
  }


  /**
    * Gets the state of the command with the supplied name
    * @param name
    * @return
    */
  def state(name:String): CmdState = {
    val result =
      if(!contains(name)){
        CmdFuncs.errorState(name, "Not found")
      }
      else {
        cmdLookup(name).lastStatus()
      }
    result
  }
}
