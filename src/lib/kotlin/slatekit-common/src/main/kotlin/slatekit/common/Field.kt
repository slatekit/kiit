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

package slatekit.common


/**
 * Used to annotate a field as an Id ( primary key with optional name
 * and whether or not it should be auto-generated
 */
annotation class Id(
        val generated:Boolean = true,
        val name:String = ""
)


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
    val eg: String = ""
)
