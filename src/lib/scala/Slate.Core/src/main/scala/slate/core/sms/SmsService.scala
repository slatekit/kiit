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

import slate.common.templates.Templates
import slate.common.{Result, BoolMessage,Vars}
import slate.common.Strings.{isNullOrEmpty}
import slate.common.results.ResultFuncs._

abstract class SmsService(templates:Option[Templates] = None) {

  /**
   * Sends the message
    *
    * @param msg : message to send
   * @return
   * @note      : implement in derived class that can actually send the message
   */
  def send(msg: SmsMessage): Result[Boolean]


  /**
   * sends a message via an IO wrapper that can be later called.
    *
    * @param message     : message to send
   * @param countryCode : destination phone country code
   * @param phone       : destination phone
   */
  def send(message:String, countryCode:String, phone:String):Result[Boolean] =
  {
    val result = validate(countryCode, phone)
    if(result.success) {
      send(new SmsMessage(message, countryCode, phone))
    }
    else{
      err(result.message)
    }
  }


  /**
   * sends a message using template and variables supplied using an IO wrapper that can be called
    *
    * @param name        : name of the template
   * @param countryCode : destination phone country code
   * @param phone       : destination phone
   * @param variables   : values to replace the variables in template
   */
  def sendUsingTemplate(name:String, countryCode:String, phone:String, variables:Vars):Result[Boolean] =
  {
    val result = validate(countryCode, phone)
    if(result.success) {
      templates.fold(err("templates are not setup"))(t => {
        val result = t.resolveTemplateWithVars(name, Option(variables.asMap()))
        val message = result.get
        send(new SmsMessage(message, countryCode, phone))
      })
    }
    else {
      err(result.message)
    }
  }


  private def validate( countryCode:String, phone:String): BoolMessage = {
    if(isNullOrEmpty(countryCode )) new BoolMessage(false, "country code not provided"    )
    else if(isNullOrEmpty(phone) )  new BoolMessage(false, "phone not provided" )
    else BoolMessage.True
  }
}
