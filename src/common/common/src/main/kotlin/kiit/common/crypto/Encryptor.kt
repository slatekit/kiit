/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github 
 *  </kiit_header>
 */

package kiit.common.crypto

import kiit.common.convert.B64

/**
 * Provides a simple facade for encryption using AES
 * @param key : The secret key ( must be 16, or 32 bytes long )
 * @param iv : The iv ( must be 16 or 32 bytes long )
 */
open class Encryptor(private val key: String,
                     private val iv: String,
                     private val b64: B64) {

    fun encrypt(text: String): String = EncryptorAES.encrypt(key, iv, text, b64)

    fun decrypt(encrypted: String): String = EncryptorAES.decrypt(key, iv, encrypted, b64)
}
