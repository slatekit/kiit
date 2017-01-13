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
package slate.integration

import slate.common.{NoResult, Result, Vars}
import slate.core.apis.ApiAction

class PushApi {


  /**
   * Registers a new template
   * @param name    : The name of the template
   * @param content : The content of the template
   */
  @ApiAction(name = "", desc= "get info about the application", roles= "@parent", verb = "@parent", protocol = "@parent")
  def addTemplate(name:String, content:String):Unit =
  {

  }


  /**
   * sends a message using the template and variables supplied
   * @param name        : name of the template
   * @param countryCode : destination phone country code
   * @param phone       : destination phone
   * @param vars        : values to replace the variables in template ( extra args on command line
   *                      will be automatically added into this collection )
   */
  @ApiAction(name = "", desc= "get info about the application", roles= "@parent", verb = "@parent", protocol = "@cli")
  def sendUsingTemplate(name:String, countryCode:String, phone:String, vars:Vars):Unit =
  {
  }


  /**
   * sends a message
   * @param message     : message to send
   * @param countryCode : destination phone country code
   * @param phone       : destination phone
   */
  @ApiAction(name = "", desc= "get info about the application", roles= "@parent", verb = "@parent", protocol = "@parent")
  def send(message:String, countryCode:String, phone:String):Result[Boolean] =
  {
    NoResult
  }
}
