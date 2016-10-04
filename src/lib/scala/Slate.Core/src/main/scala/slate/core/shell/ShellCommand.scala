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
class ShellCommand(val area:String, val name:String, val action:String, val line:String, val args:Args)
{
  var result:Result[Any] = NoResult


  /**
    * the area, name and action combined.
    * @return
    */
  def fullName :String =
  {
    if(Strings.isNullOrEmpty(name))
      return area

    if(Strings.isNullOrEmpty(action))
      return area + "." + name

    area + "." + name + "." + action
  }


  /**
    * whether or not this matches the area, naem, action supplied.
    * @param area
    * @param name
    * @param action
    * @return
    */
  def is(area:String, name:String, action:String):Boolean = {
    if(Strings.isMatch(this.area, area)
      && Strings.isMatch(this.name, name)
      && Strings.isMatch(this.action, action)){
      return true
    }
    false
  }
}
