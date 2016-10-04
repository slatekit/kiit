package slate.core.cloud

/**
  * Created by kreddy on 2/23/2016.
  */
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
