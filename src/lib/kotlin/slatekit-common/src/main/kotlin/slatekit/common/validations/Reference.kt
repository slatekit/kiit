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

package slatekit.common.validations

interface Reference {
    val value: String
}

/**
 * Represents a reference to a specific field
 *
 * @param name
 * @param original
 */
data class RefField(val name: String, val original: String = "") : Reference {
    override val value = original
}

/**
 * Represents a reference to a specific field
 *
 * @param name
 * @param original
 */
data class RefItem(val id: String, val name: String, val original: String = "") :
    Reference {
    override val value = original
}

/**
 * Represents a reference to a specific row/column field.
 * @param row
 * @param col
 * @param name
 * @param original
 */
data class RefCell(val row: Int, val col: Int, val name: String, val original: String = "") :
    Reference {
    override val value = original
}

