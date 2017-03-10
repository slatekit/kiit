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
import slate.common._
import slate.common.Strings.{isNullOrEmpty}
import slate.common.results.ResultFuncs._

/**
  * Sms Service base class with support for templates and countries
  * @param templates : The templates for the messages
  * @param ctns : The supported countries ( Defaults to US )
  */
abstract class SmsService(val templates:Option[Templates] = None,
                          ctns:Option[List[CountryCode]] = None) {

  /**
    * Default the supported countries to just USA
    */
  val countries = Country.filter( ctns.getOrElse(List(CountryCode("US"))) ).map( c => c.iso -> c).toMap


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


  /**
    * massages the phone number to include the iso code if not supplied
    * @param iso
    * @param phone
    * @return
    */
  def massagePhone(iso:String, phone:String):Result[String] = {
    val finalIso = Option(iso).getOrElse("").toUpperCase

    val result = validate(finalIso, phone)

    // Case 1: Invalid params
    if( !result.success) {
      failure(Option(result.message))
    }
    // Case 2: Invalid iso or unsupported
    else if(!countries.contains(finalIso)) {
      failure(Option(s"$finalIso is not a valid country code"))
    }
    // Case 3: Inputs valid so massage
    else {
      val country = countries(finalIso)
      val finalPhone = if(!phone.startsWith(country.phone)) {
        s"${country.iso}${country.phone}"
      } else {
        phone
      }
      success(finalPhone)
    }
  }


  private def validate( countryCode:String, phone:String): BoolMessage = {
    if(isNullOrEmpty(countryCode )) BoolMessage(false, "country code not provided"    )
    else if(isNullOrEmpty(phone) )  BoolMessage(false, "phone not provided" )
    else BoolMessage.True
  }
}
