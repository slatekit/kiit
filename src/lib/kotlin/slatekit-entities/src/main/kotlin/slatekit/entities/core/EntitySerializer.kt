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

package slatekit.entities.core

import slatekit.common.serialization.Serializer
import slatekit.common.serialization.SerializerJson
import slatekit.common.serialization.SerializerProps


class EntitySerializer {

    fun toStringProps(item: Entity, mapper: EntityMapper): String {
        return toString(SerializerProps(), item, mapper)
    }


    fun toStringJson(item: Entity, mapper: EntityMapper): String {
        return toString(SerializerJson(), item, mapper)
    }


    fun toString(serializer: Serializer, item: Entity, mapper: EntityMapper): String {
        val content = serializer.serialize(item)
        return content
    }
}
