/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2016 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.ext.security

import slate.core.apis.{Api, ApiAction}
import slate.core.common.svcs.ApiWithSupport

/**
  * Created by kreddy on 3/23/2016.
  */
@Api(area = "app", name = "security", desc = "api to encryption and decryption",
  roles= "?", auth = "app", verb = "*", protocol = "*")
class SecurityApi
  extends ApiWithSupport
{

  @ApiAction(name = "", desc= "encryptes the text", roles= "")
  override def encrypt(text:String):String =
  {
    service.encrypt(text)
  }


  @ApiAction(name = "", desc= "decrypts the text", roles= "")
  override def decrypt(text:String):String =
  {
    service.decrypt(text)
  }


  @ApiAction(name = "", desc= "encrypts all delimited tokens supplied", roles= "")
  def encryptKeyValue(key:String, value:String): String =
  {
    service.encryptKeyValue(key, value)
  }


  @ApiAction(name = "", desc= "encrypts all delimited tokens supplied", roles= "")
  def decryptKeyValue(key:String, value:String): String =
  {
    service.decryptKeyValue(key, value)
  }


  @ApiAction(name = "", desc= "encrypts all settings in the file path supplied", roles= "")
  def encryptSettingsFile(path:String):String = {
    service.encryptSettingsFile(path)
  }


  @ApiAction(name = "", desc= "decrypts all settings in the file path supplied", roles= "")
  def decryptSettingsFile(path:String):String = {
    service.decryptSettingsFile(path)
  }


  @ApiAction(name = "", desc= "encrypts all delimited tokens supplied", roles= "")
  def encryptTokens(name:String, value:String): List[String] =
  {
    service.encryptTokens(name, value)
  }


  protected def service : SecurityService = {
    val svc = new SecurityService()
    svc.ctx = this.context
    svc
  }
}