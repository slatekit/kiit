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

import kiit.common.Ignore

interface EncryptSupport {

    val encryptor: Encryptor?

    @Ignore
    fun encrypt(text: String): String {
        val result = encryptor?.let { e -> e.encrypt(text) }
        return result ?: text
    }

    @Ignore
    fun decrypt(encrypted: String): String {
        val result = encryptor?.let { e -> e.decrypt(encrypted) }
        return result ?: encrypted
    }
}
