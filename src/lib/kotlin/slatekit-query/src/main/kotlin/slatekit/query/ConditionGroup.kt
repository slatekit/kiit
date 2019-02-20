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

data class ConditionGroup(val left: Any, val operator: String, val right: Any) : ICondition {

    override fun toStringQuery(): String = getString(left) + " " + operator + " " + getString(right)

    private fun getString(item: Any?): String =
            if (item == null) {
                ""
            } else (item as? ICondition)?.toStringQuery() ?: item.toString()
}
