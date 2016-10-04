/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2016 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */
package slate.core.mobile

import slate.common.{Ensure, Result}

abstract class MessageServiceBase {

  /**
    * Sends the message
    *
    * @param msg : message to send
    * @return
    * @note      : implement in derived class that can actually send the message
    */
  def send(msg:slate.core.mobile.Message):Result[Boolean]


  /**
    * sends a message
    *
    * @param deviceId : destination device id
    * @param platform : platform ( "and", "ios", "win" )
    * @param message  : message to send
    */
  def send(deviceId:String, platform:String, message:String):Result[Boolean] =
  {
    Ensure.isNotEmptyText(deviceId, "device id not provided")
    Ensure.isNotEmptyText(platform, "platform not provided")
    Ensure.isNotEmptyText(message, "message not provided")

    send(new Message(deviceId, platform, data = message))
  }
}
