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

package slate.core.cloud

trait CloudActions {

  def execute(source:String, action:String, tag:String = "", audit:Boolean = false,
              rethrow:Boolean = false, data:Option[Any], call:() => Unit ): Unit =
  {
    try
    {
      call()
    }
    catch {
      case ex:Exception =>
      {
        onError(source, action, tag, data, Some(ex))

        if(rethrow)
        {
          throw ex
        }
      }
    }
  }


  def onAudit(source:String, action:String, tag:String, data:Option[Any]): Unit =
  {
  }


  def onError(source:String, action:String, tag:String,  data:Option[Any], ex:Option[Exception]): Unit =
  {
  }


  def onWarn(source:String, action:String, tag:String, data:Option[Any], ex:Option[Exception]): Unit =
  {
  }
}
