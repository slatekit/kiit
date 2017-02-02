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

import slate.common.{Result}
import slate.common.Require._

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
    requireText(deviceId, "device id not provided")
    requireText(platform, "platform not provided")
    requireText(message, "message not provided")

    send(new Message(deviceId, platform, data = message))
  }
}
