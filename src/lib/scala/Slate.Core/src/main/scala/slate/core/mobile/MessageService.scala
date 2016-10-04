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

package slate.core.mobile

import slate.common.queues.QueueSource
import slate.common.{DateTime, Ensure, Result}
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import scala.collection.mutable.Map

class MessageService(val _queue:Option[QueueSource] = None) extends MessageServiceBase {
  protected val _services = Map[String,MessageServiceBase]()
  protected val _platforms = List[String](Message.PLATFORM_ANDROID, Message.PLATFORM_IPHONE)


  /**
    * register a new provider
    *
    * @param platform
    * @param messageService
    */
  def register(platform:String, messageService: MessageService):Unit =
  {
    Ensure.isNotNull(platform, "platform id not valid")
    Ensure.isOneOfSupplied(platform, _platforms, "platform is not valid")

    _services(platform) = messageService
  }


  /**
    * sends via a userid, destination id. this needs to be implemented in derived classes.
 *
    * @param userId
    * @param destId
    * @param msg
    */
  def send(userId:String, destId:String, msg:Message):Result[Boolean] = {
    throw new NotImplementedException()
    null
  }


  def confirmDevice(countryCode:String, platform:String, destId:String, confirmCode:String): Result[Boolean] = {
    val msg = Message.regConfirm()
      .source("server", countryCode)
      .from("-", "-", "-")
      .to("-", "-", "-", platform, destId)
      .message("reg confirm", "confirmation of device", confirmCode, DateTime.today())
    send(msg)
  }


  def queue(msg:Message):Unit = {
    if(!_queue.isDefined) {
      return
    }
  }


  /**
    * Sends the message
    *
    * @param msg : message to send
    * @return
    * @note      : implement in derived class that can actually send the message
    */
  override def send(msg:slate.core.mobile.Message):Result[Boolean] = {
    val svc = _services(msg.toPlatform)
    svc.send(msg)
  }
}
