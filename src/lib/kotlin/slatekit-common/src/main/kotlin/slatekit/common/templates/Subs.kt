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

package slatekit.common.templates

import slatekit.common.DateTime
import slatekit.common.utils.ListMap


/**
 * Performs dynamic substitutions of variables in text.
 * Similar to interpolated strings, but at runtime. This allows for creating
 * @param items
 * @param setDefaults
 */
class Subs(items: List<Pair<String, (TemplatePart) -> String>>? = null, setDefaults: Boolean = true) {

    private val _groups = ListMap(items ?: listOf())


    init {

        // Add the default subs/variables
        if (setDefaults) {
            defaults()
        }

        // Add the custom variables
        items?.let { it ->
            it.forEach { entry -> _groups.add(entry.first, entry.second) }
        }
    }

    /**
     * whether this contains a substitution with the given key
     *
     * @param key
     * @return
     */
    fun contains(key: String): Boolean = _groups.contains(key)


    /**
     * Size of the substitutions
     * @return
     */
    val size: Int get() = _groups.size


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
            if (!_groups.contains(key)) {
                ""
            }
            else {
                val sub = _groups[key]
                sub?.invoke(TemplatePart(key, TemplateConstants.TypeText, -1, -1)) ?: ""
            }


    private fun defaults(): Unit {

        // Default functions.
        _groups.add("today", { _ -> DateTime.today().toStringYYYYMMDD() })
        _groups.add("yesterday", { _ -> DateTime.today().plusDays(-1).toStringYYYYMMDD() })
        _groups.add("tomorrow", { _ -> DateTime.today().plusDays(1).toStringYYYYMMDD() })
        _groups.add("t", { _ -> DateTime.today().toStringYYYYMMDD() })
        _groups.add("t-1", { _ -> DateTime.today().plusDays(-1).toStringYYYYMMDD() })
        _groups.add("t+1", { _ -> DateTime.today().plusDays(1).toStringYYYYMMDD() })
        _groups.add("today+1", { _ -> DateTime.today().plusDays(1).toStringYYYYMMDD() })
        _groups.add("today-1", { _ -> DateTime.today().plusDays(-1).toStringYYYYMMDD() })
    }
}
