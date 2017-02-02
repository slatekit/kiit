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

package slate.integration

import slate.common.{Vars, Result}
import slate.core.apis.{Api, ApiAction}
import slate.core.apis.svcs.ApiWithSupport
import slate.core.sms.{SmsService}

@Api(area = "infra", name = "sms", desc = "api to send emails", roles= "ops", auth="key-roles", verb = "*", protocol = "*")
class SmsApi(private val svc:SmsService) extends ApiWithSupport
{

  /**
   * sends a message
   * @param message     : message to send
   * @param countryCode : destination phone country code
   * @param phone       : destination phone
   */
  @ApiAction(name = "", desc= "send an sms", roles= "@parent", verb = "@parent", protocol = "@parent")
  def send(message:String, countryCode:String, phone:String):Result[Boolean] =
  {
    this.svc.send(message, countryCode, phone).run()
  }


  /**
   * sends a message using the template and variables supplied
   * @param name        : name of the template
   * @param countryCode : destination phone country code
   * @param phone       : destination phone
   * @param vars        : values to replace the variables in template ( extra args on command line
   *                      will be automatically added into this collection )
   */
  @ApiAction(name = "", desc= "send an sms using a template", roles= "@parent", verb = "@parent", protocol = "cli")
  def sendUsingTemplate(name:String, countryCode:String, phone:String, vars:Vars):Result[Boolean] =
  {
    this.svc.sendUsingTemplate(name, countryCode, phone, vars).run()
  }
}
