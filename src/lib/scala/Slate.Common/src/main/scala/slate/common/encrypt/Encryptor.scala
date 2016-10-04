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

package slate.common.encrypt

/**
 * Provides a simple facade for encryption using AES
 * @param _key : The secret key ( must be 16, or 32 bytes long )
 * @param _iv  : The iv ( must be 16 or 32 bytes long )
 */
class Encryptor(private val _key:String, private val _iv:String) {


  def encrypt(text:String ):String = {
    EncryptorAES.encrypt(_key, _iv, text)
  }


  def decrypt(encrypted:String):String = {
    EncryptorAES.decrypt(_key, _iv, encrypted)
  }
}
