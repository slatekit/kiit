/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * 
 *
 *  </kiit_header>
 */

package kiit.common.crypto

import kiit.common.convert.B64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Encryption implementation using AES
 */
object EncryptorAES {

    fun encrypt(key: String, iv: String, text: String, b64: B64): String {
        val ivSpec = IvParameterSpec(iv.toByteArray())
        val keySpec = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        val encrypted = cipher.doFinal(text.toByteArray())
        return b64.encode(encrypted.toTypedArray().toByteArray())
    }

    fun decrypt(key: String, iv: String, text: String, b64: B64): String {
        val ivSpec = IvParameterSpec(iv.toByteArray())
        val keySpec = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
        val decoded = b64.decode(text)
        val decryptedBytes = cipher.doFinal(decoded)
        val decrypted = String(decryptedBytes)
        return decrypted
    }
}
