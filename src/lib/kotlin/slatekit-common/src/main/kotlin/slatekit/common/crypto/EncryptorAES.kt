/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.common.crypto

import slatekit.common.convert.B64
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
