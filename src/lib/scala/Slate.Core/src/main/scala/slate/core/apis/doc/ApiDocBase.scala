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

import slate.common.console.ConsoleWriter
import slate.core.apis.{Api, ApiArg}

abstract class ApiDocBase extends ApiVisit {
  private val _writer = new ConsoleWriter()
  private val _settings = new ApiDocSettings()
  import _writer._

  def settings:ApiDocSettings =
  {
    _settings
  }


  def lineBreak():Unit =
  {
    text("---------------------------------------------------------------", endLine = true)
  }


  override def onVisitSeparator(): Unit =
  {
    line()
  }


  override def onVisitAreasBegin():Unit =
  {
    lineBreak()
    title("supported areas: ", endLine = true)
    line()
  }


  override def onVisitAreasEnd():Unit =
  {
    text("type '{area} ?' to list all apis in the area. ")
    url("e.g. sys ?" , endLine = true)
    lineBreak()
  }


  override def onVisitAreaBegin(area:String):Unit =
  {
    highlight(area, endLine = true)
  }


  override def onVisitAreaEnd(area:String):Unit =
  {
  }


  override def onVisitApisBegin(area:String):Unit =
  {
    lineBreak()
    title("supported apis: ", endLine = true)
    line()
  }


  override def onVisitApisEnd(area:String):Unit =
  {
    line()
    text("type {area}.{api} ? to list all actions on an api. ")
    url("e.g. sys.models ?" , endLine = true)
    lineBreak()
  }


  override def onVisitApiEnd(api: Api): Unit =
  {
    line()
  }


  override def onVisitApiArgEnd(arg: ApiArg): Unit =
  {
    line()
  }


  override def onVisitApiActionSyntax(): Unit =
  {
    line()
    text("type {area}.{api}.{action} ? to list inputs for an action. ")
    url("e.g. sys.models.install ?" , endLine = true)
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
