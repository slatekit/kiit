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

package slatekit.common.serialization

import slatekit.common.Serializer


/**
 * Created by kishorereddy on 6/3/17.
 */
class SerializerJson(objectSerializer: ((Serializer, Any, Int) -> Unit)? = null ) : Serializer(objectSerializer) {

    override val standardizeResult = true
}