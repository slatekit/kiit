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
