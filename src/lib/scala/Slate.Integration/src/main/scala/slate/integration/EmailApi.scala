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

import slate.common.{Vars, Files, Result}
import slate.core.apis.{Api, ApiAction}
import slate.core.common.svcs.ApiWithSupport
import slate.core.email.EmailService

@Api(area = "app", name = "emails", desc = "api to send emails", roles= "ops", auth="key-roles", verb = "*", protocol = "*")
class EmailApi(private val svc:EmailService) extends ApiWithSupport
{

  /**
   * Registers a new template
   * @param name    : The name of the template
   * @param content : The content of the template
   */
  @ApiAction(name = "", desc= "get info about the application", roles= "@parent", verb = "@parent", protocol = "@parent")
  def addTemplate(name:String, content:String):Unit =
  {
    this.svc.addTemplate(name, content)
  }


  /**
   * sends a message using the template and variables supplied
   * @param name    : name of the template
   * @param to      : The destination email address
   * @param subject : The subject of email
   * @param html    : Whether or not the email is html formatted
   * @param vars    : values to replace the variables in template ( extra args on command line
   *                      will be automatically added into this collection )
   */
  @ApiAction(name = "", desc= "get info about the application", roles= "@parent", verb = "@parent", protocol = "@cli")
  def sendUsingTemplate(name:String, to:String, subject:String, html:Boolean, vars:Vars):Unit =
  {
    this.svc.sendUsingTemplate(name, to, subject, html, vars)
  }


  /**
   * Sends the email message
   * @param to      : The destination email address
   * @param subject : The subject of email
   * @param body    : The body of the email
   * @param html    : Whether or not the email is html formatted
   * @return
   */
  @ApiAction(name = "", desc= "get info about the application", roles= "@parent", verb = "@parent", protocol = "@parent")
  def send(to:String, subject:String, body:String, html:Boolean):Result[Boolean] =
  {
    this.svc.send(to, subject, body, html)
  }
}