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

import slate.common.results.ResultSupportIn
import slate.common.{Validation, Looper, Result, ConsoleWriter}

import scala.reflect.runtime.universe.{Type, typeOf}

class ArgsSchema extends ResultSupportIn with Validation {

  private var _items = List[Arg]()


  def any = { _items.size > 0 }


  def addText(name:String, desc:String = "", required:Boolean = false, defaultVal:String = "",
              example:String = "", exampleMany:String = "", group:String = ""  ) : ArgsSchema = {
    add(name, desc, typeOf[String], required, defaultVal, example, exampleMany, group)
  }


  def addFlag(name:String, desc:String = "", required:Boolean = false, defaultVal:String = "",
              example:String = "", exampleMany:String = "", group:String = ""  ) : ArgsSchema = {
    add(name, desc, typeOf[String], required, defaultVal, example, exampleMany, group)
  }


  def addNumber(name:String, desc:String = "", required:Boolean = false, defaultVal:String = "",
                example:String = "", exampleMany:String = "", group:String = ""  ) : ArgsSchema = {
    add(name, desc, typeOf[String], required, defaultVal, example, exampleMany, group)
  }


  def add(name:String, desc:String = "", dataType:Type, required:Boolean = false, defaultVal:String = "",
          example:String = "", exampleMany:String = "", group:String = ""  ) : ArgsSchema = {
    val typeName = dataType.typeSymbol.name.toString
    _items = _items :+ new Arg("", name, desc, typeName, required, false, false, false, group, "", defaultVal, example, exampleMany )
    this
  }


  def validate(args:Args):Result[Boolean] = {

    validate( yes(), _items)( (res, arg) =>

      if (arg.isRequired && !args.containsKey(arg.name)) {
        no ( msg = Some(s"arg: ${arg.name} was not supplied") )
      }
      else {
        res
      }
    )
  }


  def maxLengthOfName : Int = {

    // Get the max length
    var maxLength = 0
    for(arg <- _items){
      if(arg.name.length > maxLength){
        maxLength = arg.name.length
      }
    }
    maxLength
  }


  def buildHelp(prefix:Option[String] = Some("-"),
                separator:Option[String] = Some("=")
                ):Unit = {

    // For color and semantic writing
    val writer = new ConsoleWriter()
    val maxLen = maxLengthOfName
    for(arg <- _items) {
      arg.toStringCLI(Some(writer), Some("\t"), prefix, separator, Some(maxLen))
      writer.line()
    }

  }
}
