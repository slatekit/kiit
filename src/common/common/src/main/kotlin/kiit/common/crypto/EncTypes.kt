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


abstract class EncType {
    abstract val enc: String
    val empty: Boolean = enc.isNullOrEmpty()
}

/**
 * Value class to represent a decrypted integer.
 * NOTE: This is useful as a parameter especially for meta programming
 * used in the universal APIs
 * @param value
 */
data class EncInt(override val enc: String, val value: Int) : EncType() {
    companion object
}

/**
 * Value class to represent a decrypted long.
 * NOTE: This is useful as a parameter especially for meta programming
 * used in the universal APIs
 * @param value
 */
data class EncLong(override val enc: String, val value: Long) : EncType() {
    companion object
}

/**
 * Value class to represent a decrypted Double.
 * NOTE: This is useful as a parameter especially for meta programming
 * used in the universal APIs
 * @param value
 */
data class EncDouble(override val enc: String, val value: Double) : EncType() {
    companion object
}

/**
 * Value class to represent a decrypted string.
 * NOTE: This is useful as a parameter especially for meta programming
 * used in the universal APIs
 * @param value
 */
data class EncString(override val enc: String, val value: String) : EncType() {
    companion object
}
