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

package slatekit.apis

import slatekit.apis.tools.docs.Doc
import slatekit.apis.tools.docs.DocConsole
import slatekit.apis.tools.docs.DocWeb
import slatekit.common.encrypt.Encryptor
import slatekit.common.naming.Namer
import slatekit.common.requests.Request
import slatekit.meta.Deserializer


/**
 * Server Settings
 * @param protocol: Protocol for server ( CLI, Web, File, Queue )   : requests are validated against this
 * @param naming  : Naming convention applied to actions ( routes ) : uses raw method names if not supplied
 * @param decoder : Decoder to provide the Deserializer for requests: uses default if not supplied
 * @param encoder : Encoder to convert an ApiResult to JSON;        : uses default if not supplied
 * @param docKey  : Documentation API key ( for help/doc requests )
 * @param docGen  : Documentation generator
 */
data class ApiSettings(
        val protocol: Protocol = Protocol.Web,
        val naming  : Namer? = null,
        val decoder : ((Request, Encryptor?) -> Deserializer)? = null,
        val encoder : ((String, Any?) -> String)? = null,
        val docKey  : String? = null,
        val docGen  : Doc = doc(protocol)
) {
    companion object {
        fun doc(protocol: Protocol):Doc {
            return when(protocol) {
                is Protocol.Web -> DocWeb()
                else -> DocConsole()
            }
        }
    }
}