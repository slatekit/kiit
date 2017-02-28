/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2016 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */
package slate.core.shell

import slate.common.{Result, Strings}
import slate.common.console.ConsoleWriter
import slate.core.shell.ShellConstants._

class ShellView(val _writer:ConsoleWriter,
                val _infoCallback:(Boolean, (Int,(String,Any)) => Unit) => Unit
               ){

  /**
    * Shows general help info
    */
  def showHelp()
  {
    _writer.title("Please type your commands")
    _writer.line()

    _writer.tab(1)
    _writer.highlight("Syntax")
    showHelpCommandSyntax()

    _writer.tab(1)
    _writer.highlight("Examples")
    showHelpCommandExample()

    _writer.tab(1)
    _writer.highlight("Available")
    showHelpExtended()

    _writer.line()
    _writer.important("type 'exit' or 'quit' to quit program")
    _writer.url("type 'info' for detailed information")
    _writer.success("type '?'                 : to list all areas")
    _writer.success("type 'area ?'            : to list all apis in an area")
    _writer.success("type 'area.api ?'        : to list all actions in an api")
    _writer.success("type 'area.api.action ?' : to list all parameters for an action")
    _writer.line()
  }


  /**
    * Shows help command structure
    */
  def showHelpCommandSyntax()
  {
    _writer.tab(1)
    _writer.text("area.api.action  -key=value*")
    _writer.line()
  }


  /**
    * Shows help command example syntax
    */
  def showHelpCommandExample()
  {
    _writer.tab(1)
    _writer.text("app.users.activate -email=johndoe@gmail.com -role=user")
    _writer.line()
  }


  /**
    * Shows extra help - useful for derived classes to show more help info
    */
  def showHelpExtended()
  {
  }


  def showAbout() : Unit = {
    _writer.line()

    _infoCallback(false, (maxLength, item) => {
      _writer.text(Strings.pad(item._1, maxLength) + " : " + item._2)
    })

    _writer.line()
  }


  /**
    * Shows help for the command
    *
    * @param cmd
    * @param mode
    */
  def showHelpFor(cmd:ShellCommand, mode:Int): Unit =
  {
    _writer.text("help for : " + cmd.fullName)
  }


  /**
    * shows error related to arguments
    *
    * @param message
    */
  def showArgumentsError(message:Option[String]): Unit =
  {
    _writer.important("Unable to parse arguments")
    _writer.important("Error : " + message.getOrElse(""))
  }
}
