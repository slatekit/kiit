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

package slate.core.apis.doc

import slate.common.{ReflectedArg, Strings, ConsoleWriter}
import slate.core.apis.{Api, ApiAction, ApiArg}

class ApiDocConsole extends ApiDocBase {
  protected val _writer = new ConsoleWriter()


  override def result: AnyRef =
  {
    null
  }


  override def isOutputSupported: Boolean =
  {
    false
  }


  override def onVisitApiBegin(api: Api): Unit =
  {
    _writer.highlight(getFormattedText(api.name, settings.maxLengthApi + 3), endLine = false)
    _writer.text(":", endLine = false)
    _writer.text(api.desc, endLine = false)
  }


  override def onVisitApiActionBegin(action: ApiAction, name:String): Unit =
  {
    _writer.tab(1)
    _writer.subTitle(getFormattedText(name, settings.maxLengthAction + 3), endLine = false)
    _writer.text(":", endLine =  false)
    _writer.text(action.desc, endLine = false)
  }


  override def onVisitApiActionEnd(action: ApiAction, name:String): Unit =
  {
    _writer.line()
  }


  override def onVisitApiActionExample(api: Api, actionName: String, action: ApiAction,
                                       args:List[ReflectedArg]): Unit =
  {
    _writer.line()
    _writer.tab(1)
    val fullName = api.area + "." + api.name + "." + actionName
    var text = ""
    for(argInfo <- args)
    {
      text = text + "-" + argInfo.name + "=" + argInfo.sample() + " "
    }
    _writer.url(fullName + " ", endLine = false)
    _writer.text(text, true)
    _writer.line()
  }


  override def onVisitApiArgBegin(arg: ApiArg): Unit =
  {
    _writer.line()
    _writer.tab(2)
    _writer.highlight(getFormattedText(arg.name, settings.maxLengthArg + 3), endLine = false)
    _writer.text(":", endLine = false)
    _writer.text( Strings.valueOrDefault(arg.desc, "\"\""), endLine = true )

    _writer.tab(2)
    _writer.text(getFormattedText("", settings.maxLengthArg + 5), endLine = false)

    val text = if(arg.required) "!" else "?"
    if(arg.required)
    {
      _writer.important(text, endLine = false)
      _writer.text("required", endLine = false)
    }
    else
    {
      _writer.text(text, endLine = false)
      _writer.text("optional", endLine = false)
    }
  }
}
