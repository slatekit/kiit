/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

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

import slate.common.{NoResult, Result, Strings}
import slate.common.args.Args

/**
  * Represents a user command typed on the command line. Contains both the method and parameters.
  * Supported format is "area.name.action" -key=value * e.g.
  * app.users.activate -email=john@gmail.com -status="active"
  * NOTE: The "area.name.action" format makes the shell command compatible with the Api Routing
  * format of the API module ( to support protocol independent APIs )
  * @param area   : The area in the method
  * @param name   : The name in the method
  * @param action : The action in the method
  * @param line   : The raw line of text supplied by user.
  * @param args   : The arguments supplied.
  */
case class ShellCommand( area:String,
                    name:String,
                    action:String,
                    line:String,
                    args:Args,
                    result:Result[Any] = NoResult )
{

  /**
    * the area, name and action combined.
    * @return
    */
  def fullName :String = {
    if(Strings.isNullOrEmpty(name))
      area
    else if(Strings.isNullOrEmpty(action))
      area + "." + name
    else
      area + "." + name + "." + action
  }


  /**
    * whether or not this matches the area, naem, action supplied.
    * @param area
    * @param name
    * @param action
    * @return
    */
  def is(area:String, name:String, action:String):Boolean =
  {
    (
         Strings.isMatch(this.area, area)
      && Strings.isMatch(this.name, name)
      && Strings.isMatch(this.action, action)
    )
  }
}


object ShellCommand {

  def apply(args:Args, line:String):ShellCommand =
  {
    val area = args.getVerb(0)
    val name = args.getVerb(1)
    val action = args.getVerb(2)
    new ShellCommand(area, name, action, line, args)
  }
}
