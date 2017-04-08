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
import slate.common.reflect.ReflectedArg
import slate.core.apis.{ApiArg, ApiAction, Api}

/**
 * Generates help docs for the web.
 * TODO: Refactor this code a bit. May be able to use
 * recursion/tail-rec instead of some of the remnant visitor pattern.
 */
class DocWeb extends Doc {
  protected val _writer = new ConsoleWriter()


  override def onApiBegin(api: Api): Unit =
  {
    _writer.highlight(api.name, endLine = false)
    _writer.text(":", endLine = false)
    _writer.text(api.desc)
  }


  override def onApiEnd(api: Api): Unit =
  {
    _writer.line()
  }


  override def onApiActionBegin(action: ApiAction, name:String): Unit =
  {
    _writer.tab(1)
    _writer.subTitle(action.name, endLine = false)
    _writer.text(":", endLine =  false)
    _writer.text(action.desc, endLine = false)
  }


  override def onApiActionEnd(action: ApiAction, name:String): Unit =
  {
    _writer.line()
  }


  override def onApiActionExample(api: Api, actionName: String, action: ApiAction,
                                       args:List[ReflectedArg]): Unit =
  {
    _writer.tab(1)
    _writer.subTitle(action.name, endLine = false)
    _writer.text(":", endLine =  false)
    _writer.text(action.desc, endLine = false)
  }


  override def onArgBegin(arg: ApiArg): Unit =
  {
    _writer.line()
    _writer.tab(1)
    _writer.text(getFormattedText("", settings.maxLengthAction + 3))
    _writer.subTitle(arg.name, endLine = false)
    _writer.text(":", endLine = false)
    _writer.text(arg.desc, endLine = false)

    _writer.line()
    _writer.tab(1)
    _writer.text(getFormattedText("", settings.maxLengthAction + 3))
    _writer.text(getFormattedText("", settings.maxLengthAction + 3))
    val text = if(arg.required) "!" else "?"
    if(arg.required)
    {
      _writer.important(text)
      _writer.text("required")
    }
    else
    {
      _writer.text(text)
      _writer.text("optional")
    }
  }
}
