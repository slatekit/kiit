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

package slatekit.query

class Condition(@JvmField val field: Any,
                @JvmField val comparison: String,
                @JvmField val fieldValue: Any) : ICondition {

    /**
     * string represention of condition
     * @return
     */
    override fun toStringQuery(): String = toStringQueryWithOptions(false, "[", "]")

    /**
     * Returns a String representation of this instance.
     * @param surround : True to surround alias with text.
     * @param left : Left surrounding text
     * @param right : Right surrounding text
     * @return
     */
    fun toStringQueryWithOptions(surround: Boolean = false, left: String = "", right: String = ""): String {
        val fieldName = QueryEncoder.ensureField(this.field.toString())
        val col = if (surround) left + fieldName + right else fieldName
        val comp = QueryEncoder.ensureCompare(comparison)
        val fieldVal = this.fieldValue
        val result = QueryEncoder.convertVal(fieldVal)

        return "$col $comp $result"
    }
}
