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

import slatekit.common.utils.B64

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
