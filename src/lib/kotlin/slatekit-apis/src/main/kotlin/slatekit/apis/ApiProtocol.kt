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

/**
 * Used to indicate the protocol(s) allowed for the ApiContainer
 * @param name
 */
abstract class ApiProtocol(val name: String)


/**
 * Used to setup an ApiContainer to allow any protocol
 */
object ApiProtocolAny : ApiProtocol(ApiConstants.ProtocolAny)


/**
 * Used to setup an ApiContainer to allow only the CLI ( command line interface ) protocol
 */
object ApiProtocolCLI : ApiProtocol(ApiConstants.ProtocolCLI)


/**
 * Used to setup an ApiContainer to allow only web/http protocol
 */
object ApiProtocolWeb : ApiProtocol(ApiConstants.ProtocolWeb)
