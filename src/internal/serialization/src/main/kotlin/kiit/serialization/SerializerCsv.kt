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

package kiit.serialization

import kiit.common.newline

/**
 * Created by kishorereddy on 6/3/17.
 */

class SerializerCsv(
    objectSerializer: ((Serializer, Any, Int) -> Unit)? = null,
    isoDates: Boolean = false
)
    : Serializer(objectSerializer, isoDates) {

    /**
     * handler for when a container item has started
     */
    override fun onContainerStart(item: Any, type: ParentType, depth: Int) {
    }

    /**
     * handle for when a container item has ended
     */
    override fun onContainerEnd(item: Any, type: ParentType, depth: Int) {
        if (depth <= 2) {
            buff.append(newline)
        }
    }

    override fun onMapItem(item: Any, depth: Int, pos: Int, key: String, value: Any?) {
        if (pos > 0 && depth <= 2) {
            buff.append(", ")
        }
        serializeValue(value, depth)
    }

    override fun onListItem(item: Any, depth: Int, pos: Int, value: Any?) {
        if (pos > 0 && depth <= 0) {
            buff.append(", ")
        }
        serializeValue(value, depth)
    }
}
