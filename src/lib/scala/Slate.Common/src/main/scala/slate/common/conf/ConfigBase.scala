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

package slate.common.conf

import slate.common.encrypt.Encryptor
import slate.common._

/**
 * Base class to get config settings with support for :
 * 1. decrypting settings
 * 2. location of config file ( resources, user directory )
 * 3. mapping settings from objects ( db connection, login, apiCredentials )
 * 4. referencing settings in other files
 *
 * @param _encryptor: Optional encryptor for decrypting encrypted config settings.
 * @note : The reason for the root folder is to be able to :
 *         1. store some settings externally from the main config file
 *         2. store some settings in a global company specific folder in the user directory
 *         3. added security by saving settings in the user directory
 *         4. centralizing common company wide / project wide settings
 *         5. examples:
 *            - {user}/{my-company}/db.conf
 *            - {user}/{my-company}/email.conf
 * @note : So the folder structure can be :
 *
 *         1. company wide database       : {user}/{my-company}/db-master.conf
 */
abstract class ConfigBase(
                           protected val _encryptor:Option[Encryptor] = None
                          )
  extends Inputs with ConfigSupport {

  def isEncrypted = _encryptor.isDefined


  def raw:Any = ???



  override def getString(key: String) : String =
  {
    val value =getObjectAs[String](key)
    if(value.startsWith("@{decrypt('")){
      val end = value.indexOf("')}")
      val encrypted = value.substring(11, end)
      _encryptor.fold[String]( encrypted ) ( enc => {
        enc.decrypt(encrypted)
      })
    }
    else
      value
  }


  override def getStringOrElse(key: String, defaultVal:String) : String =
  {
    if (containsKey(key)) getString(key) else defaultVal
  }


  def getStringEnc(key: String ) : String =
  {
    if(!isEncrypted) {
      getStringOrElse(key, "")
    }
    else {
      val encrypted = getStringOrElse(key, "")
      if (Strings.isNullOrEmpty(encrypted)) {
        ""
      }
      else {
        _encryptor.get.decrypt(encrypted)
      }
    }
  }


  def loadFrom(file:Option[String]): Option[ConfigBase] = {
    None
  }


  override def config:ConfigBase = this
}
