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
  * The result of command ( cmd ) that was run
  * @param name     : Name of the command
  * @param success  : Whether it was successful
  * @param message  : Message for success/error
  * @param result   : A resulting return value
  * @param totalMs  : Total time in milliseconds
  * @param start    : The start time of the command
  * @param end      : The end time of the command
  * @param runCount : The total time the command was run
  */
case class CmdResult(
                       name    : String          ,
                       success : Boolean         ,
                       message : String          ,
                       result  : Option[AnyRef]  ,
                       totalMs : Long            ,
                       start   : DateTime        ,
                       end     : DateTime        ,
                       runCount: Int
                    )
{
}
