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

package kiit.common.values

import kiit.common.ext.splitToMapWithPairs

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
        fun of(text: String): Vars {
            return if (text.isNullOrEmpty()) {
                Vars(listOf())
            } else {
                val data = text.splitToMapWithPairs()
                val buf = mutableListOf<Pair<String, Any>>()
                data.forEach { p -> buf.add(Pair(p.key, p.value as Any)) }
                Vars(buf.toList())
            }
        }
    }
}
