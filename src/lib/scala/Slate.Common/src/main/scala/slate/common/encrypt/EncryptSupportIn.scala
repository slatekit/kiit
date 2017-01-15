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

trait EncryptSupportIn {

  protected def enc(): Option[Encryptor] = None


  protected def encrypt(text: String): String = {
    val result = enc().flatMap[String](e => Some(e.encrypt(text)))
    result.getOrElse(text)
  }


  protected def decrypt(encrypted: String): String = {
    val result = enc().flatMap[String](e => Some(e.decrypt(encrypted)))
    result.getOrElse(encrypted)
  }
}
