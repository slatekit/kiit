/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package kiit.utils.templates

import kiit.common.DateTimes
import kiit.common.ext.toStringYYYYMMDD
import kiit.common.values.ListMap

/**
 * Performs dynamic substitutions of variables in text.
 * Similar to interpolated strings, but at runtime. This allows for creating
 * @param items
 * @param setDefaults
 */
class Subs(items: List<Pair<String, (TemplatePart) -> String>>? = null, setDefaults: Boolean = true) {

    private val groups = ListMap(items ?: listOf())

    init {

        // Add the default subs/variables
        if (setDefaults) {
            defaults()
        }

        // Add the custom variables
        items?.let { it ->
            it.forEach { entry -> groups.add(entry.first, entry.second) }
        }
    }

    /**
     * whether this contains a substitution with the given key
     *
     * @param key
     * @return
     */
    fun contains(key: String): Boolean = groups.contains(key)

    /**
     * Size of the substitutions
     * @return
     */
    val size: Int get() = groups.size

    /**
     * gets the value with the supplied key
     *
     * @param key
     * @return
     */
    operator fun get(key: String): String = lookup(key)

    /**
     * gets the value with the supplied key
     *
     * @param key
     * @return
     */
    fun lookup(key: String): String =
            if (!groups.contains(key)) {
                ""
            } else {
                val sub = groups[key]
                sub?.invoke(TemplatePart(key, TemplateConstants.TypeText, -1, -1)) ?: ""
            }

    private fun defaults() {

        // Default functions.
        groups.add("today"    , {  DateTimes.today().toStringYYYYMMDD() })
        groups.add("yesterday", {  DateTimes.today().plusDays(-1).toStringYYYYMMDD() })
        groups.add("tomorrow" , {  DateTimes.today().plusDays(1).toStringYYYYMMDD() })
        groups.add("t"        , {  DateTimes.today().toStringYYYYMMDD() })
        groups.add("t-1"      , {  DateTimes.today().plusDays(-1).toStringYYYYMMDD() })
        groups.add("t+1"      , {  DateTimes.today().plusDays(1).toStringYYYYMMDD() })
        groups.add("today+1"  , {  DateTimes.today().plusDays(1).toStringYYYYMMDD() })
        groups.add("today-1"  , {  DateTimes.today().plusDays(-1).toStringYYYYMMDD() })
    }
}
