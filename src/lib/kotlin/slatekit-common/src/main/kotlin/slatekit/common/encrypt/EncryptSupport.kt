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

import slatekit.common.Ignore

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
