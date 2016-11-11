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

package slate.common.args

import slate.common.console.ConsoleWriter
import slate.common.results.ResultSupportIn
import slate.common.Funcs.defaultOrExecute
import slate.common.{Validation, Result}

import scala.reflect.runtime.universe.{Type, typeOf}

/**
  * stores and builds a list of 1 or more arguments which collectively represent the schema.
 *
  * @note  this schema is immutable and returns a new schema when adding additional arguments
  * @param items : the list of arguments.
  */
class ArgsSchema(val items:List[Arg] = List[Arg]()) extends ResultSupportIn with Validation {

  def any = { items.size > 0 }

  /**
    * Adds a argument of type text to the schema
 *
    * @param name        : Name of argument
    * @param desc        : Description
    * @param required    : Whether this is required or not
    * @param defaultVal  : Default value for argument
    * @param example     : Example of value shown for help text
    * @param exampleMany : Examples of values shown for help text
    * @param group       : Used to group arguments into categories
    * @return
    */
  def text(name:String, desc:String = "", required:Boolean = false, defaultVal:String = "",
              example:String = "", exampleMany:String = "", group:String = ""  ) : ArgsSchema = {
    add(name, desc, typeOf[String], required, defaultVal, example, exampleMany, group)
  }


  /**
    * Adds a argument of type boolean to the schema
 *
    * @param name        : Name of argument
    * @param desc        : Description
    * @param required    : Whether this is required or not
    * @param defaultVal  : Default value for argument
    * @param example     : Example of value shown for help text
    * @param exampleMany : Examples of values shown for help text
    * @param group       : Used to group arguments into categories
    * @return
    */
  def flag(name:String, desc:String = "", required:Boolean = false, defaultVal:String = "",
              example:String = "", exampleMany:String = "", group:String = ""  ) : ArgsSchema = {
    add(name, desc, typeOf[Boolean], required, defaultVal, example, exampleMany, group)
  }


  /**
    * Adds a argument of type number to the schema
 *
    * @param name        : Name of argument
    * @param desc        : Description
    * @param required    : Whether this is required or not
    * @param defaultVal  : Default value for argument
    * @param example     : Example of value shown for help text
    * @param exampleMany : Examples of values shown for help text
    * @param group       : Used to group arguments into categories
    * @return
    */
  def number(name:String, desc:String = "", required:Boolean = false, defaultVal:String = "",
                example:String = "", exampleMany:String = "", group:String = ""  ) : ArgsSchema = {
    add(name, desc, typeOf[Number], required, defaultVal, example, exampleMany, group)
  }


  /**
    * Adds a argument to the schema
 *
    * @param name        : Name of argument
    * @param desc        : Description
    * @param dataType    : Data type of the argument
    * @param required    : Whether this is required or not
    * @param defaultVal  : Default value for argument
    * @param example     : Example of value shown for help text
    * @param exampleMany : Examples of values shown for help text
    * @param group       : Used to group arguments into categories
    * @return
    */
  def add(name:String, desc:String = "", dataType:Type, required:Boolean = false, defaultVal:String = "",
          example:String = "", exampleMany:String = "", group:String = ""  ) : ArgsSchema = {
    val typeName = dataType.typeSymbol.name.toString
    val updates = items :+ new Arg("", name, desc, typeName, required, false, false, false, group, "", defaultVal, example, exampleMany )
    new ArgsSchema(updates)
  }


  def validate(args:Args):Result[Boolean] = {

    validateResults[Arg,Boolean]( yes(), items)( (arg) =>
      if (arg.isRequired && !args.containsKey(arg.name)) {
        no ( msg = Some(s"arg: ${arg.name} was not supplied") )
      }
      else {
        yes()
      }
    )
  }


  /**
   * whether or not the argument supplied is missing
 *
   * @param args
   * @param arg
   * @return
   */
  def missing(args:Args, arg:Arg): Boolean = {
    arg.isRequired && !args.containsKey(arg.name)
  }


  /**
   * gets the maximum length of an argument name from all arguments
 *
   * @return
   */
  def maxLengthOfName : Int = {
    defaultOrExecute( items.isEmpty, 0, {
      items.maxBy( arg => arg.name.length ).name.length
    })
  }


  def buildHelp(prefix:Option[String] = Some("-"),
                separator:Option[String] = Some("=")
                ):Unit = {

    // For color and semantic writing
    val writer = new ConsoleWriter()
    val maxLen = maxLengthOfName

    for(arg <- items) {
      val semanticHelp = arg.semantic(Some("\t"), prefix, separator, Some(maxLen))
      writer.writeItems(semanticHelp)
    }
  }
}
