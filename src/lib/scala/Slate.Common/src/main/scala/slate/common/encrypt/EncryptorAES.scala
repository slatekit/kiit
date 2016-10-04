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
package slate.common.encrypt


import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}

/**
 * Encryption implementation using AES
 */
object EncryptorAES {

  def encrypt(key:String, iv:String, text:String ):String = {
      val ivSpec = new IvParameterSpec(iv.getBytes("UTF-8"))
      val keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES")
      val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
      cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
      val encrypted = cipher.doFinal(text.getBytes())
      base64Encode(encrypted)
  }


  def decrypt(key:String, iv:String, text:String):String = {
    val ivSpec = new IvParameterSpec(iv.getBytes("UTF-8"))
    val keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES")
    val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
    cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
    val decoded = base64Decode(text)
    val decryptedBytes = cipher.doFinal(decoded)
    val decrypted = new String(decryptedBytes)
    decrypted
  }


  protected def base64Encode(bytes:Array[Byte]): String = {
    Base64.getEncoder().withoutPadding().encodeToString(bytes)
  }


  protected def base64Decode(text:String): Array[Byte] = {
    Base64.getDecoder().decode(text)
  }

  /*
  def keyToSpec(key: String): SecretKeySpec = {
    var keyBytes: Array[Byte] = (SALT + key).getBytes("UTF-8")
    val sha: MessageDigest = MessageDigest.getInstance("SHA-1")
    keyBytes = sha.digest(keyBytes)
    keyBytes = util.Arrays.copyOf(keyBytes, 16)
    new SecretKeySpec(keyBytes, "AES")
  }

  private val SALT: String =
    "12345678901234567890123456789012345678901234567890"
  */
}
