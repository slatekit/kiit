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

package slatekit.common.encrypt

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Encryption implementation using AES
 */
object EncryptorAES {

    fun encrypt(key: String, iv: String, text: String, b64:B64): String {
        val ivSpec = IvParameterSpec(iv.toByteArray())
        val keySpec = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        val encrypted = cipher.doFinal(text.toByteArray())
        return b64.encode(encrypted.toTypedArray().toByteArray())
        //return base64Encode(encrypted.toTypedArray())
    }

    fun decrypt(key: String, iv: String, text: String, b64:B64): String {
        val ivSpec = IvParameterSpec(iv.toByteArray())
        val keySpec = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
        //val decoded = base64Decode(text)
        val decoded = b64.decode(text)
        val decryptedBytes = cipher.doFinal(decoded)
        val decrypted = String(decryptedBytes)
        return decrypted
    }

//    private fun base64Encode(bytes: Array<Byte>): String =
//            Base64.getEncoder().withoutPadding().encodeToString(bytes.toByteArray())
//
//    private fun base64Decode(text: String): Array<Byte> =
//            Base64.getDecoder().decode(text.toByteArray()).toTypedArray()
}
