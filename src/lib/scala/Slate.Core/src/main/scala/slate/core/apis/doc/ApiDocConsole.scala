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

import slate.common.console.ConsoleWriter
import slate.common.{ReflectedArg, Strings}
import slate.core.apis.{Api, ApiAction, ApiArg}

class ApiDocConsole extends ApiDocBase {
  protected val _writer = new ConsoleWriter()
  import _writer._

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
    highlight(getFormattedText(api.name, settings.maxLengthApi + 3), endLine = false)
    text(":", endLine = false)
    text(api.desc, endLine = false)
  }


  override def onVisitApiActionBegin(action: ApiAction, name:String): Unit =
  {
    tab(1)
    subTitle(getFormattedText(name, settings.maxLengthAction + 3), endLine = false)
    text(":", endLine =  false)
    text(action.desc, endLine = false)
  }


  override def onVisitApiActionEnd(action: ApiAction, name:String): Unit =
  {
    line()
  }


  override def onVisitApiActionExample(api: Api, actionName: String, action: ApiAction,
                                       args:List[ReflectedArg]): Unit =
  {
    line()
    tab(1)
    val fullName = api.area + "." + api.name + "." + actionName
    var txt = ""
    for(argInfo <- args)
    {
      txt = txt + "-" + argInfo.name + "=" + argInfo.sample() + " "
    }
    url(fullName + " ", endLine = false)
    text(txt, true)
    line()
  }


  override def onVisitApiArgBegin(arg: ApiArg): Unit =
  {
    line()
    tab(2)
    highlight(getFormattedText(arg.name, settings.maxLengthArg + 3), endLine = false)
    text(":", endLine = false)
    text( Strings.valueOrDefault(arg.desc, "\"\""), endLine = true )

    tab(2)
    text(getFormattedText("", settings.maxLengthArg + 5), endLine = false)

    val txt = if(arg.required) "!" else "?"
    if(arg.required)
    {
      important(txt, endLine = false)
      text("required", endLine = false)
    }
    else
    {
      text(txt, endLine = false)
      text("optional", endLine = false)
    }
  }
}
