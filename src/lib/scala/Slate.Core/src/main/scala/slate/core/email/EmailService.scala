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

package slate.core.email


import slate.common.{ApiCredentials, Vars, Ensure, Result}


abstract class EmailService {

  protected var _queueDefault = new EmailQueue()
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
   * Sends the email message
   * @param msg
   * @return
   */
  def send(msg:EmailMessage):Result[Boolean]


  /**
   * Sends the email message
   * @param to      : The destination email address
   * @param subject : The subject of email
   * @param body    : The body of the email
   * @param html    : Whether or not the email is html formatted
   * @return
   */
  def send(to:String, subject:String, body:String, html:Boolean):Result[Boolean] =
  {
    Ensure.isNotEmptyText(subject, "subject not provided")
    send( new EmailMessage(to, subject, body, html) )
  }


  /**
   * sends a message using the template and variables supplied
   * @param to      : The destination email address
   * @param subject : The subject of email
   * @param html    : Whether or not the email is html formatted
   * @param variables   : values to replace the variables in template
   */
  def sendUsingTemplate(name:String, to:String, subject:String, html:Boolean, variables:Vars):Unit =
  {
    Ensure.isTrue(_templates.contains(name), s"Template ${name} does not exist")
    Ensure.isNotEmptyText(to, "destination code not provided")
    Ensure.isNotEmptyText(subject, "subject not provided")

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
    send(to, subject, message, html)
  }
}
