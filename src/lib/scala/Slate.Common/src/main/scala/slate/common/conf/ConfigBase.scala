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

import slate.common._

/**
 * Base class to get config settings with support for :
  * 1. decrypting settings
  * 2. referencing settings in other files
  * 3. mapping settings from objects ( db connection, login, apiCredentials ) via ConfigSupport trait
 *
 * @param _encryptor: Optional encryptor for decrypting encrypted config settings.
 */
abstract class ConfigBase( protected val _encryptor:Option[(String) => String] = None )
  extends Inputs with ConfigSupport {


  /**
    * access to raw config object. e.g. could be a type-safe config.
    * @return
    */
  def raw:Any = ???


  /**
    * Extends the config by supporting decryption via marker tags.
    * e.g.
    *  db.connection = "@{decrypt('8r4AbhQyvlzSeWnKsamowA')}"
    *
    * @param key : key: The name of the config key
    * @return
    */
  override def getString(key: String) : String =
  {
    val value = getObject(key).getOrElse("").toString
    InputFuncs.decrypt(value, _encryptor)
  }


  /**
    * Loads a new config file from the file path supplied.
    * Derived classes can override this to load configs of their own type.
    *
    * NOTE: An example could be
    *     db
    *     {
    *       location: "user://myapp/conf/db.conf"
    *     }
    * @param file
    * @return
    */
  def loadFrom(file:Option[String]): Option[ConfigBase] = None


  /**
    * To support convenience methods
    * @return
    */
  override def config:ConfigBase = this
}
