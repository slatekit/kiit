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

package slate.core.sms


import slate.common.queues.QueueSource
import slate.common.{Vars, Ensure, Result}


abstract class SmsService {

  protected var _queueDefault = new SmsQueue()
  protected val _queues = Map[String, QueueSource]()
  protected val _templates = Map[String, String]()


  /**
   * registers a new template
   * @param name    : name of template
   * @param content : content of template
   */
  def addTemplate(name:String, content:String):Unit =
  {
  }


  /**
   * registers a new queue to store the messages for processing later
   * @param name  : name of the queue
   * @param queue : the queue to store the messages to
   */
  def addQueue(name:String, queue:QueueSource):Unit =
  {
  }


  /**
   * Sends the message
   * @param msg : message to send
   * @return
   * @note      : implement in derived class that can actually send the message
   */
  def send(msg:SmsMessage):Result[Boolean]


  /**
   * sends a message
   * @param message     : message to send
   * @param countryCode : destination phone country code
   * @param phone       : destination phone
   */
  def send(message:String, countryCode:String, phone:String):Result[Boolean] =
  {
    Ensure.isNotEmptyText(countryCode, "country code not provided")
    Ensure.isNotEmptyText(phone, "phone not provided")
    Ensure.isNotEmptyText(message, "message not provided")

    send(new SmsMessage(message, countryCode, phone))
  }


  /**
   * sends a message using the template and variables supplied
   * @param name        : name of the template
   * @param countryCode : destination phone country code
   * @param phone       : destination phone
   * @param variables   : values to replace the variables in template
   */
  def sendUsingTemplate(name:String, countryCode:String, phone:String, variables:Vars):Unit =
  {
    Ensure.isTrue(_templates.contains(name), s"Template ${name} does not exist")
    Ensure.isNotEmptyText(countryCode, "country code not provided")
    Ensure.isNotEmptyText(phone, "phone not provided")

    // Get the template content
    var message = _templates(name)

    // Build the message replacing the variables
    for(variable <- variables.keys())
    {
      val value = variables(variable)
      val valueText = if(value == null) "" else value.toString
      message = message.replaceAll("${" + variable + "}", valueText)
    }
    // Send the message
    send(message, countryCode, phone)
  }
}
