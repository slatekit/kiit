/**
 *  <kiit_header>
 * url: www.slatekit.com
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package kiit.apis

import kiit.apis.tools.docs.Doc
import kiit.apis.tools.docs.DocConsole
import kiit.apis.tools.docs.DocWeb
import kiit.common.Source
import kiit.common.crypto.Encryptor
import kiit.utils.naming.Namer
import kiit.requests.Request
import kiit.serialization.deserializer.Deserializer
import org.json.simple.JSONObject

/**
 * Server Settings
 * @param source: Protocol for server ( CLI, Web, File, Queue )   : requests are validated against this
 * @param naming : Naming convention applied to actions ( routes ) : uses raw method names if not supplied
 * @param decoder : Decoder to provide the Deserializer for requests: uses default if not supplied
 * @param encoder : Encoder to convert an ApiResult to JSON;        : uses default if not supplied
 * @param docKey : Documentation API key ( for help/doc requests )
 * @param docGen : Documentation generator
 */
data class Settings(
    val source: Source = Source.API,
    val naming: Namer? = null,
    val encoder: ((String, Any?) -> String)? = null,
    val record : Boolean = false,
    val docKey: String? = null,
    val docGen: () -> Doc = { doc(source) }
) {
    companion object {
        fun doc(protocol: Source): Doc {
            return when (protocol) {
                is Source.Web -> DocWeb()
                is Source.API -> DocWeb()
                else -> DocConsole()
            }
        }
    }
}
