/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
</slate_header>
  */

package slate.core.apis

/**
 * Used to indicate the protocol(s) allowed for the ApiContainer
 * @param name
 */
abstract class ApiProtocol(val name:String)


/**
 * Used to setup an ApiContainer to allow any protocol
 */
case object ApiProtocolAny extends ApiProtocol(ApiConstants.ProtocolAny)


/**
 * Used to setup an ApiContainer to allow only the CLI ( command line interface ) protocol
 */
case object ApiProtocolCLI extends ApiProtocol(ApiConstants.ProtocolCLI)


/**
 * Used to setup an ApiContainer to allow only web/http protocol
 */
case object ApiProtocolWeb extends ApiProtocol(ApiConstants.ProtocolWeb)
