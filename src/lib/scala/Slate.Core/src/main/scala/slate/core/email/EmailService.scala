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

package slate.core.email


import slate.common.templates.Templates
import slate.common._
import slate.common.IO._
import slate.common.Strings.{isNullOrEmpty}


abstract class EmailService(templates:Option[Templates] = None) {


  /**
   * Sends the email message
   * @param msg
   * @return
   */
  def send(msg:EmailMessage):IO[Result[Boolean]]


  /**
   * Sends the email message
   * @param to      : The destination email address
   * @param subject : The subject of email
   * @param body    : The body of the email
   * @param html    : Whether or not the email is html formatted
   * @return
   */
  def send(to:String, subject:String, body:String, html:Boolean): IO[Result[Boolean]] =
  {
    // NOTE: This guards are more readable that other alternatives
    val result = validate(to, subject)
    if(result.success){
      send( new EmailMessage(to, subject, body, html) )
    }
    else
    {
      failedIO(result.message)
    }
  }


  /**
   * sends a message using the template and variables supplied
   * @param to      : The destination email address
   * @param subject : The subject of email
   * @param html    : Whether or not the email is html formatted
   * @param variables   : values to replace the variables in template
   */
  def sendUsingTemplate(name:String, to:String, subject:String, html:Boolean, variables:Vars):IO[Result[Boolean]] =
  {
    val result = validate(to, subject)
    if(result.success) {
      // Send the message
      //send(to, subject, message, html)
      templates.fold(failedIO[Boolean]("templates are not setup"))(t => {
        val result = t.resolveTemplateWithVars(name, Option(variables.asMap()))
        val message = result.get
        send(new EmailMessage(to, subject, message, html))
      })
    }
    else {
      failedIO(result.message)
    }
  }


  private def validate(to:String, subject:String): BoolMessage = {
    if(isNullOrEmpty(to)           ) new BoolMessage(false, "to not provided"      )
    else if(isNullOrEmpty(subject) ) new BoolMessage(false, "subject not provided" )
    else BoolMessage.True
  }
}
