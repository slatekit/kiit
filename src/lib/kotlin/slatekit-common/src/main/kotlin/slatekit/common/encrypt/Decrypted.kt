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

/**
 * Value class to represent a decrypted integer.
 * NOTE: This is useful as a parameter especially for meta programming
 * use datas such as in the API component
 * @param value
 */
data class DecInt(val value: Int)


/**
 * Value class to represent a decrypted long.
 * NOTE: This is useful as a parameter especially for meta programming
 * use datas such as in the API component
 * @param value
 */
data class DecLong(val value: Long)


/**
 * Value class to represent a decrypted Double.
 * NOTE: This is useful as a parameter especially for meta programming
 * use datas such as in the API component
 * @param value
 */
data class DecDouble(val value: Double)


/**
 * Value class to represent a decrypted string.
 * NOTE: This is useful as a parameter especially for meta programming
 * use datas such as in the API component
 * @param value
 */
data class DecString(val value: String)


