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

package slatekit.meta.models



/**
 * Used to annotate a field as Persisted/Model field.
 */
annotation class Field(
    val name: String = "",
    val desc: String = "",
    val required: Boolean = true,
    val unique: Boolean = false,
    val updatable: Boolean = true,
    val indexed: Boolean = false,
    val length: Int = 0,
    val defaultVal: String = "",
    val encrypt: Boolean = false,
    val example: String = ""
)
