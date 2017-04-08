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

package slate.core.apis.doc

import slate.common.console.ConsoleWriter
import slate.core.apis.{Api, ApiArg}

abstract class Doc extends ApiVisit {
  private val _writer = new ConsoleWriter()
  private val _settings = new DocSettings()
  import _writer._

  def settings:DocSettings =
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


  override def onAreasBegin():Unit =
  {
    lineBreak()
    title("supported areas: ", endLine = true)
    line()
  }


  override def onAreasEnd():Unit =
  {
    text("type '{area} ?' to list all apis in the area. ")
    url("e.g. sys ?" , endLine = true)
    lineBreak()
  }


  override def onAreaBegin(area:String):Unit =
  {
    highlight(area, endLine = true)
  }


  override def onAreaEnd(area:String):Unit =
  {
  }


  override def onApisBegin(area:String):Unit =
  {
    lineBreak()
    title("supported apis: ", endLine = true)
    line()
  }


  override def onApisEnd(area:String):Unit =
  {
    line()
    text("type {area}.{api} ? to list all actions on an api. ")
    url("e.g. sys.models ?" , endLine = true)
    lineBreak()
  }


  override def onApiEnd(api: Api): Unit =
  {
    line()
  }


  override def onArgEnd(arg: ApiArg): Unit =
  {
    line()
  }


  override def onApiActionSyntax(): Unit =
  {
    line()
    text("type {area}.{api}.{action} ? to list inputs for an action. ")
    url("e.g. sys.models.install ?" , endLine = true)
    lineBreak()
  }


  protected def getFormattedText(text:String, max:Int):String =
  {
    if (text.length == max)
      text
    else
      text + 0.until(max - text.length).foldLeft("")( (s, v) => s + " ")
  }
}
