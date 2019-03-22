package slatekit.apis.security

import slatekit.apis.ApiConstants

/**
 * Used to indicate the protocol(s) allowed for the ApiHost
 * @param name
 */
abstract class Protocol(val name: String)

/**
 * Used to setup an ApiHost to allow any protocol
 */
object AllProtocols : Protocol(ApiConstants.SourceAny)

/**
 * Used to setup an ApiHost to allow only the CLI ( command line interface ) protocol
 */
object CliProtocol : Protocol(ApiConstants.SourceCLI)

/**
 * Used to setup an ApiHost to allow only web/http protocol
 */
object WebProtocol : Protocol(ApiConstants.SourceWeb)

object Protocols {
    const val all = "*"
    const val cli = "cli"
    const val web = "web"
    const val file = "file"
    const val queue = "queue"
}
