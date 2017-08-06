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
 * Short hand representation of a list of "variables" backed
 * by the ListMap<String,Any> which represents both a List and Map
 * and provides lookup by both the key and index/iteration like a list
 */
class Vars(items: List<Pair<String, Any>>) : ListMap<String, Any>(items) {

    companion object {

        /**
         * converts a text of "a=1,b=2,c=3" into a Vars object
         * of
         *  - "a" -> 1
         *  - "b" -> 2
         *  - "c" -> 3
         *
         * with key=String, value=Any and separators expected to
         * be "," for pairs values and "=" between key / value
         */
        fun apply(text: String): Vars {
            return if (text.isNullOrEmpty()) {
                Vars(listOf())
            }
            else {
                val data = text.splitToMapWithPairs()
                val buf = mutableListOf<Pair<String, Any>>()
                data.forEach { p -> buf.add(Pair(p.key, p.value as Any)) }
                Vars(buf.toList())
            }
        }
    }
}
