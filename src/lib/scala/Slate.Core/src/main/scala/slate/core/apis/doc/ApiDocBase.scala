/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2016 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.core.apis.doc

import slate.common.ConsoleWriter
import slate.core.apis.{Api, ApiArg}

abstract class ApiDocBase extends ApiVisit {
  private val _writer = new ConsoleWriter()
  private val _settings = new ApiDocSettings()


  def settings:ApiDocSettings =
  {
    _settings
  }


  def lineBreak():Unit =
  {
    _writer.text("---------------------------------------------------------------", endLine = true)
  }


  override def onVisitSeparator(): Unit =
  {
    _writer.line()
  }


  override def onVisitAreasBegin():Unit =
  {
    lineBreak()
    _writer.title("supported areas: ", endLine = true)
    _writer.line()
  }


  override def onVisitAreasEnd():Unit =
  {
    _writer.text("type '{area} ?' to list all apis in the area. ")
    _writer.url("e.g. sys ?" , endLine = true)
    lineBreak()
  }


  override def onVisitAreaBegin(area:String):Unit =
  {
    _writer.highlight(area, endLine = true)
  }


  override def onVisitAreaEnd(area:String):Unit =
  {
  }


  override def onVisitApisBegin(area:String):Unit =
  {
    lineBreak()
    _writer.title("supported apis: ", endLine = true)
    _writer.line()
  }


  override def onVisitApisEnd(area:String):Unit =
  {
    _writer.line()
    _writer.text("type {area}.{api} ? to list all actions on an api. ")
    _writer.url("e.g. sys.models ?" , endLine = true)
    lineBreak()
  }


  override def onVisitApiEnd(api: Api): Unit =
  {
    _writer.line()
  }


  override def onVisitApiArgEnd(arg: ApiArg): Unit =
  {
    _writer.line()
  }


  override def onVisitApiActionSyntax(): Unit =
  {
    _writer.line()
    _writer.text("type {area}.{api}.{action} ? to list inputs for an action. ")
    _writer.url("e.g. sys.models.install ?" , endLine = true)
    lineBreak()
  }


  protected def getFormattedText(text:String, max:Int):String =
  {
    if (text.length == max)
      return text
    var pad = ""
    var count = 0
    while(count < max - text.length)
    {
      pad += " "
      count = count + 1
    }
    text + pad
  }
}
